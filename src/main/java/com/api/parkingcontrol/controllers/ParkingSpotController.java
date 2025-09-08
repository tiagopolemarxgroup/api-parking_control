package com.api.parkingcontrol.controllers;

import com.api.parkingcontrol.dtos.ParkingSpotDto;
import com.api.parkingcontrol.models.ParkingSpotModel;
import com.api.parkingcontrol.services.ParkingSpotService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping(value = "/v1")
public class ParkingSpotController {

    @Autowired
    private ParkingSpotService parkingSpotService;

    @PostMapping
    public ResponseEntity<Object> saveParkingSpot(@RequestBody @Valid ParkingSpotDto parkingSpotDto){
        if(parkingSpotService.existsByLicensePlateCar(parkingSpotDto.getLicensePlateCar())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: License plate Car is already in use");
        }
        if(parkingSpotService.existsByParkingSpotNumber(parkingSpotDto.getParkingSpotNumber())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking Spot number is already in use");
        }
        if(parkingSpotService.existsByApartamentAndBlock(parkingSpotDto.getApartament(), parkingSpotDto.getBlock())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking Spot registred number is already in use");
        }

        var parkingSpotModel = new ParkingSpotModel();
        BeanUtils.copyProperties(parkingSpotDto, parkingSpotModel);
        parkingSpotModel.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));
        return ResponseEntity.status(HttpStatus.CREATED).body(parkingSpotService.save(parkingSpotModel));
    }

    @GetMapping
    public ResponseEntity<Page<ParkingSpotModel>> findAll(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
        return ResponseEntity.ok(parkingSpotService.findAll(pageable));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Object> findByid(@PathVariable(value = "id") UUID id){
        Optional<ParkingSpotModel> parkingSpotModel = parkingSpotService.findById(id);
        if(!parkingSpotModel.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking spot not found");
         }
        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotModel.get());
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Object> update(@PathVariable("id") UUID id, @RequestBody @Valid ParkingSpotDto parkingSpotDto){
         Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
         if(!parkingSpotModelOptional.isPresent()){
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking spot not found");
         }

//         var parkingSpotModel = parkingSpotModelOptional.get();
//         parkingSpotModel.setParkingSpotNumber(parkingSpotDto.getParkingSpotNumber());
//         parkingSpotModel.setLicensePlateCar(parkingSpotDto.getLicensePlateCar());
//         parkingSpotModel.setModelCar(parkingSpotDto.getModelCar());
//         parkingSpotModel.setBrandCar(parkingSpotDto.getBrandCar());
//         parkingSpotModel.setColorCar(parkingSpotDto.getColorCar());
//         parkingSpotModel.setResponsibleName(parkingSpotDto.getResponsibleName());
//         parkingSpotModel.setApartament(parkingSpotDto.getApartament());
//         parkingSpotModel.setBlock(parkingSpotDto.getBlock());
        var parkingSpotModel = new ParkingSpotModel();
        BeanUtils.copyProperties(parkingSpotDto, parkingSpotModel);
        parkingSpotModel.setId(id);
        parkingSpotModel.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));

        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.save(parkingSpotModel));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Object> delete(@PathVariable(value = "id") UUID id){
        Optional<ParkingSpotModel> parkingSpotModel = parkingSpotService.findById(id);
        if(!parkingSpotModel.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking spot not found");
        }
        parkingSpotService.delete(parkingSpotModel.get());
        return ResponseEntity.status(HttpStatus.OK).body("Parking Spot deleted successfully");
    }

}
