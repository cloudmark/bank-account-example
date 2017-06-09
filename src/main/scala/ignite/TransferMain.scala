package ignite

import akka.actor.{Actor, ActorLogging, Props}
import akka.event.LoggingReceive

class TransferMain extends Actor with ActorLogging {

  //First create two BankAccount actors
  val accountA = context.actorOf(Props[BankAccount], "accountA")
  val accountB = context.actorOf(Props[BankAccount], "accountB")

  //send a deposit message to accountA
  accountA ! BankAccount.Deposit(100)

  //If a 'Done' message is received back, call a transfer function
  def receive = {
    case BankAccount.Done => transfer(70)
  }

  //transfer function creates a transacton actor and sends a 'Transfer' message to it between
  //accountA and accountB for the specified amount.
  def transfer(amount: BigInt): Unit = {

    val transaction = context.actorOf(Props[WireTransfer], "transfer")

    transaction ! WireTransfer.Transfer(accountA, accountB, amount)

    context.become(LoggingReceive{
      case WireTransfer.Done =>
        println("successs")
        context.stop(self)
      case WireTransfer.Failed =>
        println("failed")
        context.stop(self)
    })

  }
}