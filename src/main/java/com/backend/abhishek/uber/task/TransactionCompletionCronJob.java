package com.backend.abhishek.uber.task;

import com.backend.abhishek.uber.entities.Driver;
import com.backend.abhishek.uber.entities.Rider;
import com.backend.abhishek.uber.entities.WalletTransaction;
import com.backend.abhishek.uber.repositories.WalletTransactionRepository;
import com.backend.abhishek.uber.services.EmailSenderService;
import com.backend.abhishek.uber.utils.EmailContentBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class TransactionCompletionCronJob {

    private final WalletTransactionRepository walletTransactionRepository;
    private final ModelMapper  modelMapper;
    private final EmailSenderService emailSenderService;

    // Run every minute
    @Scheduled(cron = "0 * * * * *")
    public void runEveryMinute() {
        log.info("TransactionCompletionCronJob job running at: {}", LocalDateTime.now());

        List<WalletTransaction> nonProcessedWalletTransactions= walletTransactionRepository.findByIsEmailProcessedFalse();
        if(nonProcessedWalletTransactions.isEmpty()){
            log.info("No Wallet Transaction is pending");
        }

        for (WalletTransaction txn : nonProcessedWalletTransactions) {
            Rider rider = txn.getRide().getRider();
            Driver driver = txn.getRide().getDriver();

            String riderEmail = rider.getUser().getEmail();
            String driverEmail = driver.getUser().getEmail();

            if(txn.getTransactionId().startsWith("R")) {
                String html = EmailContentBuilder.buildDebitEmail(
                        txn.getAmount(),
                        txn.getTransactionType().name(),
                        txn.getTimeStamp(),
                        txn.getTransactionId()
                );

                emailSenderService.sendDebitHtmlEmail(riderEmail, "Transaction Details for Your  Uber Ride", html);
            } else if (txn.getTransactionId().startsWith("D")) {

                String html = EmailContentBuilder.buildCreditEmail(
                        txn.getAmount(),
                        txn.getTransactionType().name(),
                        txn.getTimeStamp(),
                        txn.getTransactionId()
                );
                emailSenderService.sendCreditHtmlEmail(driverEmail, "Transaction Details for Your  Uber Ride", html);
            }

            txn.setEmailProcessed(true); // don't forget this!
            walletTransactionRepository.save(txn);
        }

    }
}


