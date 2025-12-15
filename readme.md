+------------+         +-------------------+        +---------------------+      +--------------------+
|   Client   |   --->  |  AuthController    |  --->  |   AuthService       | ---> |    Database        |
|  (User)    |         | (Receives Request) |        | (Business Logic)    |      | (User Data)        |
+------------+         +-------------------+        +---------------------+      +--------------------+
        ^                         |                          |                           |
        |                         v                          v                           v
  +-------------------+        +-------------------+       +-------------------+       +-------------------+
  |  Response (JWT)   | <---   |   AuthController   | <---  |   AuthService      | <---  |   Database (User) |
  | (Token sent to user)      |  (Sends Response)  |       | (Returns Token)    |       |   (User Data)     |
  +-------------------+        +-------------------+       +-------------------+       +-------------------+
