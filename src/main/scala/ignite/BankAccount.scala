package ignite

import akka.actor.Actor
import akka.event.LoggingReceive

object BankAccount {

  case class Deposit(amount: BigInt) {
    require(amount > 0)
  }

  case class Withdraw(amount: BigInt) {
    require(amount > 0)
  }

  case object Done

  case object Failed

}

class BankAccount extends Actor {

  import BankAccount._

  var balance = BigInt(0)

  def receive = LoggingReceive {
    //Deposit messages add amount to balance state
    case Deposit(amount) =>
      balance += amount
      sender ! Done

    //Withdraw messages subtract amount from balance state
    case Withdraw(amount) if amount <= balance =>
      balance -= amount
      sender ! Done

    //Any other message would return a failure to the sender
    case _ => sender ! Failed
  }

}