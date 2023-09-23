package com.driver.services.impl;

import com.driver.model.Payment;
import com.driver.model.PaymentMode;
import com.driver.model.Reservation;
import com.driver.repository.PaymentRepository;
import com.driver.repository.ReservationRepository;
import com.driver.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    ReservationRepository reservationRepository2;
    @Autowired
    PaymentRepository paymentRepository2;

    @Override
    public Payment pay(Integer reservationId, int amountSent, String mode) throws Exception {
        Reservation reservation = reservationRepository2.findById(reservationId).get();
        int requiredAmount = reservation.getNumberOfHours()*reservation.getSpot().getPricePerHour();
        if(amountSent<requiredAmount){
            throw new Exception("Insufficient Amount");
        }
        mode = mode.toUpperCase();
        PaymentMode paymentMode;
        if(mode!=PaymentMode.CARD.toString() || mode!=PaymentMode.UPI.toString() || mode!=PaymentMode.CASH.toString()){
            throw new Exception("Payment mode not detected");
        }
        else{
            if(mode=="CARD") paymentMode = PaymentMode.CARD;
            else if(mode=="CASH") paymentMode = PaymentMode.CASH;
            else paymentMode = PaymentMode.UPI;
        }
        Payment payment = new Payment();
        payment.setPaymentCompleted(true);
        payment.setPaymentMode(paymentMode);
        payment.setReservation(reservation);

        Payment savedPayment = paymentRepository2.save(payment);

        reservation.setPayment(savedPayment);
        reservationRepository2.save(reservation);

        return savedPayment;
    }
}
