package Tasaheel.paymentManagement.repositories;

import Tasaheel.paymentManagement.models.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Wallet findByUserId(Long userId);
}
