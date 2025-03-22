create table menu_items (
    id uuid primary key,
    restaurant_id uuid not null references restaurants(id),
    name text not null,
    description text not null,
    price_cents bigint not null
)