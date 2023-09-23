package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {
        Optional<User> optionalUser = userRepository3.findById(userId);
        if(!optionalUser.isPresent()){
            throw new Exception("Cannot make reservation");
        }
        User user = optionalUser.get();

        Optional<ParkingLot> optionalParkingLot = parkingLotRepository3.findById(parkingLotId);
        if(!optionalParkingLot.isPresent()){
            throw new Exception("Cannot make reservation");
        }
        ParkingLot parkingLot = optionalParkingLot.get();

        Spot selectedSpot = null;
        int minPrice = Integer.MAX_VALUE;
        for(Spot spot:parkingLot.getSpotList()){
            int wheels = 0;
            if(spot.getSpotType()==SpotType.TWO_WHEELER) wheels = 2;
            else if (spot.getSpotType() == SpotType.FOUR_WHEELER) wheels = 4;
            else wheels = Integer.MAX_VALUE;
            int price = spot.getPricePerHour()*timeInHours;
            if(wheels>=numberOfWheels && !spot.getOccupied() && price<minPrice){
                selectedSpot = spot;
            }
        }
        if(selectedSpot==null) {
            return null;
        }
        Reservation reservation = new Reservation();
        reservation.setNumberOfHours(timeInHours);
        reservation.setSpot(selectedSpot);
        reservation.setUser(user);

        Reservation savedReservation = reservationRepository3.save(reservation);

        selectedSpot.getReservationList().add(savedReservation);
        selectedSpot.setOccupied(true);
        spotRepository3.save(selectedSpot);

        user.getReservationList().add(savedReservation);
        userRepository3.save(user);

        return savedReservation;
    }
}
