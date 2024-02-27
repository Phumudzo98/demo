package com.example.demo.Service;


import com.example.demo.Model.*;
import com.example.demo.Repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class Impl implements Interface {


    //Initialize/Declare variables/repository
    //repo
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private BusinessInfoRepository businessRepo;
    @Autowired
    private InvoiceRepository invoiceRepo;
    @Autowired
    private ItemsRepository itemRepo;
    @Autowired
    private QuoteRepository quoteRepo;
    @Autowired
    private ClientRepository clientRepo;
    @Autowired
    private ClientAddressRepository clientAddressRepo;
    @Autowired
    private MailSender mailSender;

    @Override
    @Transactional
    public boolean registerUser(User user)
    {
        Optional<User> check = userRepo.findById(user.getEmail());

        if(check.isEmpty())
        {
            user.setEmail(user.getEmail());
            userRepo.save(user);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject("Registration activation link");
            message.setText("Hello "+user.getF_name()+". \n\nHere is your link: ");
            message.setFrom("phumu98@gmail.com");
            message.setTo(user.getEmail());

            mailSender.send(message);

            return true;
        }
        return false;
    }


    @Override
    public boolean loginApp(String email, String password) {
        User user = userRepo.findByEmailAndPassword(email, password);
        if(user==null)
        {
            return false;
        }
        return user.getEmail().equals(email) && user.getPassword().equals(password);
    }

    @Override
    public boolean forgotPassword(String email) {
        User checkEmail = userRepo.findByEmail(email);

        if(checkEmail==null)
        {
            return false;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("Forgot password");
        message.setText("Dear "+checkEmail.getF_name()+" "+checkEmail.getL_name()+"\n\nHere is your link: ");
        message.setFrom("phumu98@gmail.com");
        message.setTo(email);

        mailSender.send(message);

        return true;

    }


    @Override
    public boolean deleteInvoice(int id, String email) {

        if(invoiceRepo.existsById(id))
        {
            invoiceRepo.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Invoice searchInvoice(int id, String email) {
        try
        {
            if(invoiceRepo.existsById(id))
            {
                return invoiceRepo.findById(id).orElse(null);
            }
        }
        catch (Exception e)
        {
            return null;
        }
        return null;
    }

    @Override
    public List<Invoice> homeTop5Invoice(String email) {

//

        if(invoiceRepo.findAll().isEmpty())
        {
            return Collections.emptyList();
        }

        return invoiceRepo.findAll();
    }

    @Override
    public List<Quote> homeTop5Quote(String email) {
        if(quoteRepo.findAll().isEmpty())
        {
            return Collections.emptyList();
        }
        return quoteRepo.findAll();
    }

    @Override
    @Transactional
    public boolean createInvoiceOrQuote(String email, ClientAddressInvoiceQuoteItems caiqi) {
        //Desmond
        User user = userRepo.findByEmail(email);
        double total=0;
        Client client = caiqi.getClient();
        client.setUser(user);

        ClientAddress clientAddress = caiqi.getClientAddress();
        clientAddressRepo.save(clientAddress);

        client.setClientAddress(clientAddress);

        clientRepo.save(client);


        if(caiqi.getType().equals("Invoice")) {
            Invoice invoice = caiqi.getInvoice();

            invoice.setUser(user);
            invoice.setDate(LocalDate.now());

            List<Items> items = caiqi.getItems();
            for (Items item : items) {
                item.setInvoice(invoice);
                total=total+(item.getPrice()*item.getQty());
            }

            invoice.setTotalAmount(total);
            invoiceRepo.save(invoice);

            for (Items item : items) {
                item.setInvoice(invoice);
                itemRepo.save(item);
            }
            return true;
        }
        else if (caiqi.getType().equals("Quote"))
        {
            Quote quote = caiqi.getQuote();

            quote.setUser(user);
            quote.setDate(LocalDate.now());

            List<Items> items = caiqi.getItems();
            for (Items item : items) {
                item.setQuote(quote);
                total=total+(item.getPrice()*item.getQty());
            }

            quote.setTotalAmount(total);
            quoteRepo.save(quote);

            for (Items item : items) {
                item.setQuote(quote);
                itemRepo.save(item);
            }
            return true;
        }
        return false;
    }

    @Override
    public List<Invoice> getAllInvoices(String email) {

        User user = userRepo.findByEmail(email);

        try
        {
            return invoiceRepo.findByUser(user);
        }
        catch (Exception e)
        {
            return Collections.emptyList();
        }
    }



    public List<Quote> getAllQuote(String email)
    {
        try
        {
            return quoteRepo.findAll();
        }
        catch (Exception e)
        {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean updateUserDetails(User user) {

        User updateUser = userRepo.findByEmail(user.getEmail());

        if(updateUser!=null)
        {
            updateUser.setPassword(user.getPassword());
            updateUser.setF_name(user.getF_name());
            updateUser.setL_name(user.getL_name());

            userRepo.save(updateUser);

            return true;
        }
        return false;
    }


}
