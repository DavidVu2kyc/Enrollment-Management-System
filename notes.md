Created 2 containers in seperated network 
    crazy_lichterman
    my-postgres

To make the hostname my-postgres work, you need to put both containers on a user-defined network where Docker enables "Service Discovery".
    Create the network in your terminal:
    Connect the Postgres container:
    Connect the pgAdmin container:
    Try again: In pgAdmin, use my-postgres as the hostname. It will now resolve correctly.
