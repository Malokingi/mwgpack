import pandas as pd
import numpy as np
import seaborn as sns
from matplotlib import pyplot as plt

def add_first_order_date_and_month_categories(filepath):
    df = pd.read_csv(filepath)

    # add a column that indicates the earliest order date per customer
    first_order_date_by_user = df.groupby('user_id')['order_date'].min()
    first_order_date_by_user.name = 'first_order_date'

    df = df.join(first_order_date_by_user, on='user_id')

    # make two additional columns which indicate the month during which
    # order date and first order date take place in
    df['first_order_month'] = df['first_order_date'].astype('datetime64[M]')
    df['order_month'] = df['order_date'].astype('datetime64[M]')

    return df

def add_first_activity_week_and_week_categories(filepath):
    df = pd.read_csv(filepath)

    df['activity_date'] = pd.to_datetime(df['activity_date'])

    first_activity_date = df.groupby(['user_id'])['activity_date'].min()
    first_activity_date.name = 'first_activity_date'

    df = df.join(first_activity_date, on='user_id')

    df['activity_week'] = (
        pd.to_datetime(df['activity_date'], unit='d')
            -
        pd.to_timedelta(df['activity_date'].dt.dayofweek, unit='d')
    )
    df['first_activity_week'] = (
        pd.to_datetime(df['first_activity_date'], unit='d')
            -
        pd.to_timedelta(df['first_activity_date'].dt.dayofweek, unit='d')
    )

    return df

# count the number of unique customers and total sum of order prices per month
def group_by_cohorts_total_revenue(filepath):
    df = add_first_order_date_and_month_categories(filepath)

    grouped_by_cohorts = df.groupby([
        'first_order_month',
        'order_month'
    ]).agg({
        'revenue': 'sum',
        'user_id': 'nunique'
    })

    return grouped_by_cohorts

# count the number of unique customers and number of orders per month
def group_by_cohorts_number_of_purchaces(filepath):
    df = add_first_order_date_and_month_categories(filepath)

    grouped_by_cohorts = df.groupby([
        'first_purchase_month',
        'purchase_month'
    ]).agg({
        'purchase_id': 'nunique',
        'player_id': 'nunique'
    })

    return grouped_by_cohorts

def show_purchaces_per_player_over_time_and_lifetime(filepath):
    purchases_grouped_by_cohorts = group_by_cohorts_number_of_purchaces(filepath)

    # adda column indicating the number of purchaces per player per month
    purchases_grouped_by_cohorts['purchases_per_player'] = (
        purchases_grouped_by_cohorts['purchase_id']
        / purchases_grouped_by_cohorts['player_id']
    )
    purchases_grouped_by_cohorts = purchases_grouped_by_cohorts.reset_index()

    # Add lifetime column indicating how many months, as an int, a player has been playing
    purchases_grouped_by_cohorts['cohort_lifetime'] = ((
            purchases_grouped_by_cohorts['purchase_month']
            - purchases_grouped_by_cohorts['first_purchase_month']
        ) / np.timedelta64(1, 'M')
    ).round().astype('int')

    # make a pivot table showing how purchases per player changes over
    # time and how long a player has been playing
    lifetime_pivot = purchases_grouped_by_cohorts.pivot_table(
        index='first_purchase_month',
        columns='cohort_lifetime',
        values='purchases_per_player',
        aggfunc='mean',
    )

    print(lifetime_pivot) # Print the pivot table

def make_a_avg_purchase_size_heatmap(filepath):
    revenue_per_user_pivot = pd.read_csv('/datasets/revenue_pivot.csv')
    revenue_per_user_pivot = revenue_per_user_pivot.set_index('first_order_month')

    # Set the figure size
    plt.figure(figsize=(13, 9))
    # Set the visualization title
    plt.title('Average customer purchase size')
    # Make a heatmap
    sns.heatmap(
        revenue_per_user_pivot,
        annot=True,
        fmt='.2f',
        linewidths=1,
        linecolor='black',
    )

def add_initial_user_count(df):
    initial_users_count = df[df['cohort_lifetime'] == 0][ ['first_activity_week', 'user_id'] ]
    initial_users_count = initial_users_count.rename( columns={'user_id': 'cohort_users'} ) 

    return df.merge(initial_users_count, on='first_activity_week')

def make_a_retention_rate_pivot_table(filepath):
    user_activity = add_first_activity_week_and_week_categories(filepath)

    # add lifetime column (in weeks)
    user_activity['cohort_lifetime'] = ((
            user_activity['activity_week']
                -
            user_activity['first_activity_week']
        ) / np.timedelta64(1, 'W')
    ).astype(int)

    # arrange by cohort
    cohorts = (
        user_activity.groupby(['first_activity_week', 'cohort_lifetime'])
        .agg({'user_id': 'nunique'})
        .reset_index()
    )

    cohorts = add_initial_user_count(cohorts)

    # add retention rate
    cohorts['retention'] = cohorts['user_id'] / cohorts['cohort_users']

    # make and print table
    retention_pivot = cohorts.pivot_table(
        index='first_activity_week',
        columns='cohort_lifetime',
        values='retention',
        aggfunc='sum',
    )

    print(retention_pivot)

def make_a_churn_rate_heatmap(filepath):
    cohorts = pd.read_csv(filepath)

    cohorts['churn_rate'] = cohorts.groupby(['first_event_week'])['users_count'].pct_change()

    churn_pivot = cohorts.pivot_table(
        index='first_event_week',
        columns='lifetime',
        values='churn_rate',
        aggfunc='sum',
    )

    sns.set(style='white')

    # Set the figure size
    plt.figure(figsize=(13, 9))

    # Name the visualization
    plt.title('Churn Rate')

    # Make a heatmap
    sns.heatmap(
        churn_pivot,
        annot=True,
        fmt='.1%',
        linewidths=1,
        linecolor='black'
    )

def add_first_order_and_timeline_cols(filepath):
    df = pd.read_csv(filepath)
    df['coffee_time'] = pd.to_datetime(df['coffee_time'])
    df['first_coffee_datetime'] = pd.to_datetime(
        df['first_coffee_datetime']
    )
    df['time_to_event'] = (
        df['coffee_time'] - df['first_coffee_datetime']
    )
    return df

def filter_out_orders_gt_30_days(df):
    return df[df['time_to_event'] < '30 days']

def display_ret_rate_by_behavior(filepath):
    events = add_first_order_and_timeline_cols(filepath)
    filtered_events = filter_out_orders_gt_30_days(events)

    count_events_by_users = (
        filtered_events.groupby(['user_id'])
        .agg({'coffee_time': 'count'})
        .reset_index()
    )
    count_events_by_users['is_target_behavior'] = (
        count_events_by_users['coffee_time'] > 4
    )

    user_ids_with_target_behavior = count_events_by_users.query('is_target_behavior == True')['user_id'].unique()
    user_ids_without_target_behavior = count_events_by_users.query('is_target_behavior != True')['user_id'].unique()

    events.loc[
        events['user_id'].isin(user_ids_with_target_behavior),
        'is_in_behavioral_cohort',
    ] = 'yes'

    events.loc[
        events['user_id'].isin(user_ids_without_target_behavior),
        'is_in_behavioral_cohort',
    ] = 'no'

    printRetentionRate(events[events['is_in_behavioral_cohort'] == 'yes'])


def printRetentionRate(df):
    cohorts = (
        df.groupby(['first_coffee_week', 'cohort_lifetime'], as_index=False)
        .agg({'user_id': 'nunique'})
        .sort_values(['first_coffee_week', 'cohort_lifetime'])
    )

    inital_users_count = cohorts[cohorts['cohort_lifetime'] == 0][
        ['first_coffee_week', 'user_id']
    ]
    inital_users_count = inital_users_count.rename(
        columns={'user_id': 'cohort_users'}
    )

    cohorts = cohorts.merge(inital_users_count, on='first_coffee_week')

    cohorts['retention'] = cohorts['user_id'] / cohorts['cohort_users']

    print(cohorts.groupby(['cohort_lifetime'])['retention'].mean())

    cohorts.groupby(['cohort_lifetime'])['retention'].mean().plot.bar()
