# challenge API
Implemented Money Transaction feature between two accounts
1. Create "TransactionController.java". 
with endpoint URL is "/v1/accounts/transferMoney" and Required Model Object is "Money.java" with property 'accountFrom', 'accountTo', 'amount' and amount must be greater than 0.
2. Implemented TransactionService to perform money transfer between two accounts. Call TransactionRepositoryInMemory to execute business logic.
3. Implement business logic into "TransactionRepositoryInMemory.java".
Internally call "AccountRepository" to fetch existing account details through 'accountID'.
4. In "TransactionRepositoryInMemory" used Reentrant locking mechanism to prevent from multi threaded environment.
5. Created "TransactionControllerTest.java" and "TransactionService.java" to unit test the Money Transfer functionality.
