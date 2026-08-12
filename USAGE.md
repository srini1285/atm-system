# ATM System - User Manual

## Quick Start Guide

### Starting the Application

```bash
./start.sh
```

The application will build and launch automatically. You'll see:

```
Building ATM System...
Build completed!
Starting ATM System...

====================================
    Welcome to ATM System v1.0
====================================

Type 'help' for available commands or 'exit' to quit.

ATM> 
```

### Command Reference

#### 1. LOGIN - Create or access customer account

**Syntax:**
```
login [customer_name]
```

**Examples:**
```
ATM> login Alice
Hello, Alice!
Your balance is $0

ATM> login Bob
Hello, Bob!
Your balance is $150
```

**Notes:**
- Creates a new account if customer doesn't exist
- Logs out previous user automatically
- Shows current balance upon login
- Shows any outstanding debts

---

#### 2. DEPOSIT - Add funds to your account

**Syntax:**
```
deposit [amount]
```

**Examples:**
```
ATM> deposit 100
Your balance is $100

ATM> deposit 50.50
Your balance is $150.50
```

**Behavior:**
- If you have debts, deposit is applied to settle debts first
- Remaining funds (if any) become available balance
- Shows updated balance after deposit

**Example with Debt Settlement:**
```
ATM> deposit 50
Transferred $50 to Alice
Your balance is $0
Owed $20 to Alice
```

---

#### 3. WITHDRAW - Remove funds from your account

**Syntax:**
```
withdraw [amount]
```

**Examples:**
```
ATM> withdraw 50
Your balance is $50

ATM> withdraw 100
Your balance is $0
```

**Notes:**
- Can only withdraw available balance
- Will show error if insufficient funds
- Updated balance shown after withdrawal

**Error Example:**
```
ATM> withdraw 200
Error: Insufficient funds. Your balance is $100
```

---

#### 4. TRANSFER - Send funds to another customer

**Syntax:**
```
transfer [target_customer] [amount]
```

**Examples:**
```
ATM> transfer Bob 50
Transferred $50 to Bob
Your balance is $50

ATM> transfer Alice 100
Transferred $100 to Alice
Your balance is $0
```

**Behavior:**
- Can transfer more than your current balance (creates debt)
- Creates customer if they don't exist
- Cannot transfer to yourself
- Shows your remaining balance

**Overdraft Example:**
```
ATM> transfer Alice 100
Transferred $30 to Alice
Your balance is $0
Owed $70 to Alice
```

**Recipient Side:**
When Bob deposits after receiving a transfer:
```
ATM> deposit 50
Transferred $50 to Alice
Your balance is $0
Owed $50 to Alice
```

---

#### 5. LOGOUT - End current session

**Syntax:**
```
logout
```

**Example:**
```
ATM> logout
Goodbye, Alice!
ATM> 
```

---

#### 6. HELP - Display available commands

**Syntax:**
```
help
```

---

#### 7. EXIT - Quit the application

**Syntax:**
```
exit
```

---

## Complete Example Session

```bash
$ ./start.sh

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

ATM> transfer Bob 30
Your balance is $240
Owed $10 from Bob

ATM> logout
Goodbye, Alice!

ATM> login Bob
Hello, Bob!
Your balance is $0
Owed $10 to Alice

ATM> deposit 100
Transferred $10 to Alice
Your balance is $90

ATM> logout
Goodbye, Bob!

ATM> exit
Thank you for using ATM System. Goodbye!
```

---

## Common Scenarios

### Scenario 1: Simple Deposit and Withdrawal

```
ATM> login John
Hello, John!
Your balance is $0

ATM> deposit 500
Your balance is $500

ATM> withdraw 200
Your balance is $300

ATM> logout
Goodbye, John!
```

### Scenario 2: Transfer Between Accounts

```
ATM> login Alice
Hello, Alice!
Your balance is $0

ATM> deposit 100
Your balance is $100

ATM> transfer Bob 30
Transferred $30 to Bob
Your balance is $70

ATM> logout
Goodbye, Alice!

ATM> login Bob
Hello, Bob!
Your balance is $30

ATM> logout
Goodbye, Bob!
```

### Scenario 3: Overdraft and Debt Settlement

```
ATM> login Charlie
Hello, Charlie!
Your balance is $0

ATM> transfer Diana 100
Transferred $100 to Diana
Your balance is $0
Owed $100 to Diana

ATM> deposit 60
Transferred $60 to Diana
Your balance is $0
Owed $40 to Diana

ATM> deposit 50
Transferred $40 to Diana
Your balance is $10
```

### Scenario 4: Multiple Debts

```
ATM> login Eve
Hello, Eve!
Your balance is $0

ATM> transfer Alice 50
Transferred $50 to Alice
Your balance is $0
Owed $50 to Alice

ATM> transfer Bob 30
Transferred $30 to Bob
Your balance is $0
Owed $50 to Alice
Owed $30 to Bob

ATM> deposit 100
Transferred $50 to Alice
Transferred $30 to Bob
Your balance is $20
```

---

## Error Messages & Solutions

### "Error: You must login first"
**Solution:** Type `login [your_name]` before performing any operation

### "Error: Insufficient funds"
**Solution:** 
- Check your balance: Insufficient funds means available balance is less than requested withdrawal
- Note: Transfers are allowed even with insufficient funds (creating debt)

### "Error: Cannot transfer to yourself"
**Solution:** Use a different customer name for the transfer

### "Error: Invalid amount"
**Solution:** 
- Use positive numbers only
- Format: `100` or `100.50` (not negative or text)

### "Error: Customer not found" (on transfer)
**Solution:** The system will automatically create the customer if they don't exist

### "Error: No customer logged in"
**Solution:** Login first with `login [your_name]`

---

## Special Cases

### What happens when you logout and login again?

All your data is preserved within the same session:
```
ATM> login Alice
Hello, Alice!
Your balance is $100

ATM> logout
Goodbye, Alice!

ATM> login Alice
Hello, Alice!
Your balance is $100          ← Balance is remembered
```

### What happens when you restart the application?

All data is cleared (fresh start):
```
$ ./start.sh
...
ATM> login Alice
Hello, Alice!
Your balance is $0            ← Fresh account
```

### What if I transfer more than I have?

You can transfer any amount, but it creates a debt:
```
ATM> login Bob
Hello, Bob!
Your balance is $50

ATM> transfer Alice 100
Transferred $50 to Alice
Your balance is $0
Owed $50 to Alice             ← Debt obligation created
```

When Bob deposits again, the deposit settles the debt:
```
ATM> deposit 80
Transferred $50 to Alice
Your balance is $30           ← Remaining after debt settlement
```

---

## Tips & Best Practices

1. **Check your balance**: Your balance is shown after each operation
2. **Monitor debts**: Pay attention to "Owed to/from" messages
3. **Plan transfers**: Consider available balance and outstanding debts
4. **Multiple logins**: You can login as different customers to manage their accounts
5. **Fresh starts**: Each run of `./start.sh` gives you a clean ATM system

---

## Troubleshooting

### Application won't start
- Make sure `start.sh` is executable: `chmod +x start.sh`
- Ensure Java 11+ is installed: `java -version`
- Check that `JAVA_HOME` is set: `echo $JAVA_HOME`

### Commands not recognized
- Ensure command syntax is correct (check the reference above)
- Make sure you're logged in (except for `login` command)
- Type `help` to see available commands

### Lost balance information
- This is expected! Each `start.sh` run starts with a clean system
- All data is in-memory and not persisted

### Strange balance after transfer
- Check if you had outstanding debts (they're settled from deposits first)
- Review the output messages for "Transferred" and "Owed" lines

---

## Support

For technical issues or questions about features, refer to README.md for more details about design decisions and implementation.
