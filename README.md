# ATM System

A Java-based ATM (Automated Teller Machine) simulator that demonstrates core banking operations including login, deposit, withdrawal, and fund transfers between customers.

## Features

- **Customer Management**: Create and manage customer accounts dynamically
- **Balance Management**: Track account balances with support for overdrafts
- **Fund Transfers**: Transfer funds between customers with debt tracking
- **Debt Tracking**: Track and automatically settle debts when funds are deposited
- **Clean State**: Each run starts fresh with no persisted state
- **Interactive CLI**: Command-line interface for user interaction

## Design Decisions & Assumptions

### 1. Overdraft Handling
- **Decision**: Allow customers to transfer funds they don't have, creating a debt obligation
- **Reasoning**: Provides more realistic banking simulation where overdrafts create debts that must be settled
- **Example**: Bob can transfer $100 to Alice even with $30 balance, creating a $70 debt to Alice

### 2. Automatic Debt Settlement on Deposit
- **Decision**: When a customer deposits funds and has outstanding debts, the deposit is automatically applied to settle debts first
- **Reasoning**: Realistic banking behavior - debts are prioritized before customer can access funds
- **Order**: Debts to other customers are settled in the order they were created

### 3. Bidirectional Debt Tracking
- **Decision**: System tracks both "owed to" and "owed from" relationships separately
- **Reasoning**: Allows proper display of obligations from both perspectives
- **Example**: If Bob owes Alice $40, Alice can see "Owed $40 from Bob" and Bob sees "Owed $40 to Alice"

### 4. In-Memory Storage
- **Decision**: All data is stored in memory, cleared on each application restart
- **Reasoning**: Meets requirement for clean state on each run; simplifies implementation
- **Tradeoff**: No persistence between sessions (by design)

### 5. Single-Threaded CLI Interface
- **Decision**: Sequential command processing with one logged-in user at a time
- **Reasoning**: Simulates real ATM behavior and meets CLI requirements
- **Limitation**: Only one user can be logged in simultaneously

## Project Structure

```
atm-system/
├── build.gradle                          # Gradle build configuration
├── start.sh                              # Application startup script
├── README.md                             # This file
├── USAGE.md                              # Usage instructions
├── src/
│   ├── main/java/com/atm/
│   │   ├── ATMApplication.java           # Main application entry point
│   │   ├── model/
│   │   │   ├── Customer.java             # Customer domain model
│   │   │   ├── Debt.java                 # Debt domain model
│   │   │   └── Transaction.java          # Transaction record model
│   │   ├── service/
│   │   │   ├── ATMService.java           # Core ATM operations
│   │   │   └── TransactionService.java   # Transaction processing
│   │   ├── repository/
│   │   │   └── CustomerRepository.java   # In-memory customer storage
│   │   ├── ui/
│   │   │   └── CLIInterface.java         # CLI user interface
│   │   ├── exception/
│   │   │   ├── ATMException.java         # Base exception
│   │   │   ├── InsufficientFundsException.java
│   │   │   ├── CustomerNotFoundException.java
│   │   │   ├── InvalidOperationException.java
│   │   │   └── AuthenticationException.java
│   │   └── util/
│   │       └── CurrencyFormatter.java    # Currency formatting utility
│   └── test/java/com/atm/
│       ├── service/
│       │   ├── ATMServiceTest.java
│       │   └── TransactionServiceTest.java
│       └── model/
│           └── CustomerTest.java
└── .gitignore
```

## Building and Running

### Prerequisites
- Java 11 or higher (with `JAVA_HOME` set)
- Gradle (or use the included `gradlew` if present)

### Quick Start

```bash
# Make the start script executable
chmod +x start.sh

# Run the application
./start.sh
```

### Manual Build and Run

```bash
# Build the application
gradle clean build

# Run the application
gradle run
# OR
java -cp build/classes/java/main com.atm.ATMApplication
```

## Usage

Once the application starts, you'll see the ATM prompt:
```
ATM> 
```

### Available Commands

#### Login
```
ATM> login Alice
Hello, Alice!
Your balance is $0
```

#### Deposit
```
ATM> deposit 100
Your balance is $100
```

#### Withdraw
```
ATM> withdraw 50
Your balance is $50
```

#### Transfer
```
ATM> transfer Bob 30
Transferred $30 to Bob
Your balance is $20
```

#### Logout
```
ATM> logout
Goodbye, Alice!
```

### Example Session

```bash
ATM> login Alice
Hello, Alice!
Your balance is $0

ATM> deposit 100
Your balance is $100

ATM> logout
Goodbye, Alice!

ATM> login Bob
Hello, Bob!
Your balance is $0

ATM> deposit 80
Your balance is $80

ATM> transfer Alice 50
Transferred $50 to Alice
Your balance is $30

ATM> transfer Alice 100
Transferred $30 to Alice
Your balance is $0
Owed $70 to Alice

ATM> deposit 30
Transferred $30 to Alice
Your balance is $0
Owed $40 to Alice

ATM> logout
Goodbye, Bob!

ATM> login Alice
Hello, Alice!
Your balance is $210
Owed $40 from Bob

ATM> logout
Goodbye, Alice!
```

## Error Handling

The system handles various error conditions gracefully:

- **Invalid Command**: Displays help message with available commands
- **Not Logged In**: Prompts to login before performing operations
- **Invalid Amount**: Rejects negative or non-numeric amounts
- **Customer Not Found**: Notifies when transfer target doesn't exist
- **Transfer to Self**: Prevents transferring to the same account
- **Overdraft Handling**: Allows overdraft but tracks debt

Example error handling:

```
ATM> transfer Alice 100
Error: Insufficient funds. Cannot transfer $100 from account with balance $30.
Transferred $30 to Alice
Your balance is $0
Owed $70 to Alice
```

## Testing

Run the test suite:

```bash
gradle test
```

Tests cover:
- Customer creation and retrieval
- Deposit and withdrawal operations
- Fund transfers (sufficient and insufficient funds)
- Debt creation and settlement
- Edge cases and error scenarios

## Implementation Highlights

### Clean Architecture
- Separation of concerns: Model, Service, Repository, UI layers
- Easy to extend and test
- Follows SOLID principles

### Exception Handling
- Custom exception hierarchy for different error scenarios
- Graceful error recovery with user-friendly messages
- Comprehensive validation of all inputs

### Debt Management Algorithm
1. When depositing funds for a customer with existing debts:
   - Apply deposit amount to oldest debt first
   - If deposit > debt amount, settle debt and carry over to next debt
   - If deposit < debt amount, reduce debt by deposit amount
   - Remaining funds (if any) become available balance

2. Debt is tracked bidirectionally:
   - Debtor sees "Owed X to Y"
   - Creditor sees "Owed X from Y"

### Transaction Recording
- All operations are logged with timestamps
- Transaction history can be used for auditing
- Clean separation between transaction service and ATM service

## Assumptions & Constraints

1. **No Persistence**: Data is cleared on application restart (by design)
2. **No Concurrent Access**: Single-threaded CLI - only one user logged in at a time
3. **In-Memory Storage**: All data held in memory during runtime
4. **Integer Currency**: Amounts handled as integers (cents) for precision
5. **No Interest Calculations**: Simple balance tracking without interest
6. **ASCII Currency**: Dollar symbol ($) for all displays
7. **Case Sensitivity**: Customer names are case-sensitive
8. **No Authentication**: Login by name only (no passwords)

## Special Cases Handled

1. **Transfer exceeding balance**: Creates debt obligation
2. **Deposit with existing debt**: Automatically settles debt first
3. **Transfer to non-existent customer**: Automatically creates customer
4. **Logout without login**: Shows appropriate error message
5. **Multiple debts**: Tracks and settles in order
6. **Self-transfer**: Prevented with error message

## Future Enhancements

- Database persistence
- Multi-threaded server with concurrent access
- User authentication with passwords
- PIN management
- Transaction history retrieval
- ATM network simulation
- Card management
- Fee calculations
- Interest accrual
- Withdrawal limits

## Troubleshooting

### Build Fails
- Ensure Java 11+ is installed: `java -version`
- Ensure `JAVA_HOME` environment variable is set
- Clear build directory: `gradle clean build`

### Application Doesn't Start
- Check that port is not in use (if using server mode)
- Verify Java is in PATH: `which java`
- Check logs for detailed error messages

### Commands Not Recognized
- Ensure you're logged in before performing operations (except login)
- Check command spelling and format
- Type `help` for available commands

## License

This is a demonstration project created for banking ATM simulation purposes.

## Contact & Support

For issues or questions about the implementation, refer to the code comments and exception messages.
