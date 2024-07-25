package Tasaheel.paymentManagement.repositories;

import Tasaheel.paymentManagement.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
