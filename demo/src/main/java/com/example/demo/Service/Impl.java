package com.example.demo.Service;


import com.example.demo.Model.*;
import com.example.demo.Repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.Collections;
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
            message.setText("Here is your link: ");
            message.setFrom("phumu98@gmail.com");
            message.setTo("phumu98@gmail.com");

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
    public boolean deleteInvoice(int id) {

        if(invoiceRepo.existsById(id))
        {
            invoiceRepo.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Invoice searchInvoice(int id) {
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
    public List<Invoice> homeTop5Invoice() {

        if(invoiceRepo.findAll().isEmpty())
        {
            return Collections.emptyList();
        }
        return invoiceRepo.findTop5ByOrderByDateDesc();
    }

    @Override
    public List<Quote> homeTop5Quote() {
        if(quoteRepo.findAll().isEmpty())
        {
            return Collections.emptyList();
        }
        return quoteRepo.findTop5ByOrderByDateDesc();
    }

    @Override
    @Transactional
    public boolean createInvoiceOrQuote(ClientAddressInvoiceQuoteItems caiqi) {
        //Desmond

        Client client = caiqi.getClient();
        ClientAddress clientAddress = caiqi.getClientAddress();
        Invoice invoice = caiqi.getInvoice();
        List<Items> items = caiqi.getItems();
        Quote quote =caiqi.getQuote();

        if(invoice==null)
        {
            clientRepo.save(client);
            clientAddressRepo.save(clientAddress);
            quoteRepo.save(quote);

            for(Items item: quote.getItems())
            {
                item.setQuote(quote);
                itemRepo.save(item);
            }
            return true;

        } else if (quote==null) {
            clientRepo.save(client);
            clientAddressRepo.save(clientAddress);
            invoiceRepo.save(invoice);

            for(Items item: invoice.getItems())
            {
                item.setInvoice(invoice);
                itemRepo.save(item);
            }
            return true;

        }
        return false;
    }

    @Override
    public List<Invoice> getAllInvoices() {

        try
        {
            return invoiceRepo.findAll();
        }
        catch (Exception e)
        {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean updateUserDetails(User user) {

        Optional<?> updateUser = userRepo.findByEmail(user.getEmail());
        return false;
    }


}
