# Unusual Spending Kata 

Tags: `tdd`, `test-doubles`

## Goal
The goal of this kata is to practice Discovery Testing. That is, after understanding all the requirements, to follow this workflow:

1. Start with a Collaboration Test of the feature's entry point method (provided as `spending.TriggersUnusualSpendingEmail#trigger`), using it to identify 2-4 new classes that could be used to break the problem down
2. Recurse into one of those newly-created classes, determining whether it should be broken down further:
3. If it can be broken down further, write a Collaboration Test for it, identifying more Behaviors, Values, and Wrappers
4. If it can be implemented cleanly as a simple pure function, write a Regression Test to specify and ensure its individual behavior
5. If it merely needs to delegate to a third party function, write a Wrapper for it and don't bother unit testing the wrapper
6. Repeat Step 2 until you've implemented a complete solution


## Requirements
You work at a credit card company and as a value-add they want to start providing alerts to users when their spending in any particular category is higher than usual.

* A `Payment` is a simple value object with a `price`, `description`, and `category`
* A `Category` is an enumerable type of a collection of things like "entertainment", "restaurants", and "golf"
* For a given `userId`, fetch the payments for the current month and the previous month
* Compare the total amount paid for the each month, grouped by category; filter down to the categories for which the user spent at least 50% more this month than last month
* Compose an e-mail message to the user that lists the categories for which spending was unusually high, with a subject like "Unusual spending of $1076 detected!" and this body:

```
Hello card user!

We have detected unusually high spending on your card in these categories:

* You spent $148 on groceries
* You spent $928 on travel

Love,

The Credit Card Company
```

## Caveats
Like most applications that developers are paid to write, this kata tasks the programmer to implement just one layer of an overall solution, meaning there are a number of important facets we can't control:

* We don't control who invokes our `TriggersUnusualSpendingEmail#trigger(userId)` entry point, or when; nor can we change its method signature, as it represents a public interface that something else (maybe a job scheduler system) is depending on
* We don't control how payments are fetched, that's Somebody Else's Job™; all we have is an agreed-upon contract: `spending.FetchesUserPaymentsByMonth#fetch(userId, year, month)`
* We don't control how e-mails are sent, all we know is that it's specified by the interface `spending.EmailsUser.email(userId, subject, body)`
* Instances of `FetchesUserPaymentsByMonth` are provided by a factory method, which would be painful to mock and means we'll want to write a Wrapper Object for it
* `EmailsUser.email` is a static method, which is also painful to mock and we'll want to wrap it, too

For more on mocking and external constraints, be sure to read Don't mock what you don't own.

Making forward-progress on our work while dealing with constraints like this are part-and-parcel of being a productive professional programmer. Discovery Testing is designed to enable that productivity by helping us write well-designed and well-tested code that's narrowly focused on the things within our control.

## Extra Credit
Once you've completed the kata, if you'd like to test your approach for how easy it is to change, try these requested requirement changes:

* Load three months of payment history and compare the current month to their average totals by category (you're guaranteed to have the most recent month, but either of the two prior months might come back as empty sets, as if the user lacks the payment history)
* Update the e-mail to report what the usual spending amount was, in addition to the unusual spending amount