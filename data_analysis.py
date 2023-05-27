import pandas as pd
import numpy as np

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