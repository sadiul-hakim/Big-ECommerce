1. OneToOne == One user has one address and only that use has that address. So just put the address id in the user
   table.
2. OneToMany / ManyToOne == One user can have many orders, but each order belongs to exactly one user. Add a user_id
   foreign key column in the orders table.
3. ManyToMany == A user can have many roles, and a role can belong to many users. Create a join table, e.g. user_roles(
   user_id, role_id).