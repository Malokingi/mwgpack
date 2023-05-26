import pandas as pd

# How to add a column that indicates the earliest order date per customer
# and then make two additional columns which indicate the month during which
# order date and first order date take place in
def add_first_order_date_and_month_categories(filepath):
    orders = pd.read_csv(filepath)

    first_order_date_by_customers = orders.groupby('customer_id')['order_date'].min()
    first_order_date_by_customers.name = 'first_order_date'

    orders = orders.join(first_order_date_by_customers, on='customer_id')

    orders['first_order_month'] = orders['first_order_date'].astype('datetime64[M]')
    orders['order_month'] = orders['order_date'].astype('datetime64[M]')

    orders_grouped_by_cohorts = orders.groupby([
        'first_order_month',
        'order_month'
    ]).agg({
            'revenue': 'sum',
            'customer_id': 'nunique'
        })

    print(orders_grouped_by_cohorts.head())