package Tasaheel.paymentManagement.services;

import Tasaheel.paymentManagement.models.PaymentMethod;
import Tasaheel.paymentManagement.models.TransactionStatus;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import Tasaheel.paymentManagement.models.Transaction;
import Tasaheel.paymentManagement.models.Wallet;
import Tasaheel.paymentManagement.repositories.TransactionRepository;
import Tasaheel.paymentManagement.repositories.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public boolean checkBalance(Long userId, Integer amount) {
        Wallet wallet = walletRepository.findByUserId(userId);
        return wallet != null && wallet.getBalance() >= amount;
    }

    public void initiateWallet(Long userId)
    {
        Wallet existingWallet = walletRepository.findByUserId(userId);
        if (existingWallet != null) {
            return;
        }
        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setBalance(0);
        walletRepository.save(wallet);

    }

    public ResponseEntity<String> initiateTransaction(Long senderId, Long receiverId, int amount, PaymentMethod paymentMethod) {

        Transaction transaction = new Transaction();
        transaction.setUserId(senderId);
        transaction.setProviderId(receiverId);
        transaction.setAmount(amount);
        transaction.setTransactionStatus(TransactionStatus.PENDING);
        transaction.setPaymentMethod(paymentMethod);
        transaction.setCreated(LocalDateTime.now());

        if (transaction.getPaymentMethod() == paymentMethod.wallet) {
            transactionRepository.save(transaction);
            return payByWallet(senderId, amount);
        }

        transactionRepository.save(transaction);
        return ResponseEntity.ok("Your payment will be Cash on Delivery");
    }

    @Transactional
    public ResponseEntity<String> payByWallet(Long userId, int amount) {
        Wallet wallet = walletRepository.findByUserId(userId);
        if (!checkBalance(userId, amount)) {
            return ResponseEntity.badRequest().body("Insufficient balance");
        }

        wallet.setBalance(wallet.getBalance() - amount);
        wallet.setHeldAmount(wallet.getHeldAmount() + amount);
        walletRepository.save(wallet);

        return ResponseEntity.ok("Amount has been held");
    }


    public ResponseEntity<String> processTransaction(boolean approval1 ,Long id)
    {
        if ( approval1) {
            return completeTransaction(id);
        }
        else
            return ResponseEntity.ok("If there is a problem make a Report");

    }

    public ResponseEntity<String> completeTransaction(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(() -> new RuntimeException("Transaction not found"));
        transaction.setTransactionStatus(TransactionStatus.AGREED);
        transaction.setCompleted(LocalDateTime.now());
        transactionRepository.save(transaction);

        if (transaction.getPaymentMethod() == PaymentMethod.wallet)
        {
            return transferHeldAmount( transaction.getUserId() , transaction.getProviderId() , transaction.getAmount());
        }
        else return ResponseEntity.ok("Thank you for using Tasaheel :) ");

    }

    
    @Transactional
    public ResponseEntity<String> transferHeldAmount(Long userId, Long providerId, int amount) {
        Wallet senderWallet = walletRepository.findByUserId(userId);
        Wallet recipientWallet = walletRepository.findByUserId(providerId);

        senderWallet.setHeldAmount(senderWallet.getHeldAmount() - amount);
        recipientWallet.setBalance(recipientWallet.getBalance() + amount);
        walletRepository.save(senderWallet);
        walletRepository.save(recipientWallet);

        return ResponseEntity.ok("Amount has been transferred to the recipient , Thank you for using Tasaheel :)");
    }



    public ResponseEntity<String> refundToWallet(Transaction transaction, int amount) {
        Wallet wallet1 = walletRepository.findByUserId(transaction.getProviderId());
        if (wallet1.getBalance() < amount) {
            return ResponseEntity.badRequest().body("Insufficient funds");
        }
        wallet1.setBalance(wallet1.getBalance() - amount);
        walletRepository.save(wallet1);

        Wallet wallet2 = walletRepository.findByUserId(transaction.getUserId());
        wallet2.setBalance(wallet2.getBalance() + amount);
        walletRepository.save(wallet2);

        return ResponseEntity.ok("Amount has been refunded to the wallet");
    }


}
