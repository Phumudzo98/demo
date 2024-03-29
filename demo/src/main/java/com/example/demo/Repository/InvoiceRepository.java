package com.example.demo.Repository;

import com.example.demo.Model.Invoice;
import com.example.demo.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {


    List<Invoice> findByUserEmail(String userEmail);

    List<Invoice> findTop5ByUserOrderByDateDesc(User user);

    Invoice findByInvoiceNoAndUser(int invoiceNo, User user);

    Boolean existsByInvoiceNo(int invoiceNo);

    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.user.email = :userEmail AND i.paymentStatus = 'unpaid'")
    Double getTotalUnpaidAmount(@Param("userEmail") String userEmail);



}
