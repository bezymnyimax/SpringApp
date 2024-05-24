package ru.gorbunov.springboot.app.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.gorbunov.springboot.app.dto.SensorDTO;
import ru.gorbunov.springboot.app.models.Sensor;
import ru.gorbunov.springboot.app.services.SensorService;
import ru.gorbunov.springboot.app.util.MeasurementErrorResponse;
import ru.gorbunov.springboot.app.util.MeasurementException;
import ru.gorbunov.springboot.app.util.SensorValidator;

import javax.validation.Valid;

import static ru.gorbunov.springboot.app.util.ErrorsUtil.returnErrorsToClient;

@RestController
@RequestMapping("/sensors")
@Tag(name = "Контроллер сенсора")
public class SensorsController {

    private final SensorService sensorService;
    private final ModelMapper modelMapper;
    private final SensorValidator sensorValidator;

    @Autowired
    public SensorsController(SensorService sensorService, ModelMapper modelMapper,
                             SensorValidator sensorValidator) {
        this.sensorService = sensorService;
        this.modelMapper = modelMapper;
        this.sensorValidator = sensorValidator;
    }

    @Operation(
            summary = "Регистрация сенсора",
            description = "Позволяет зарегистрировать сенсор"
    )
    @PostMapping("/registration")
    public ResponseEntity<HttpStatus> registration(@RequestBody @Valid SensorDTO sensorDTO,
                                                   BindingResult bindingResult) {
        Sensor sensorToAdd = convertToSensor(sensorDTO);

        sensorValidator.validate(sensorToAdd, bindingResult);

        if (bindingResult.hasErrors())
            returnErrorsToClient(bindingResult);

        sensorService.register(sensorToAdd);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<MeasurementErrorResponse> handleException(MeasurementException e) {
        MeasurementErrorResponse response = new MeasurementErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private Sensor convertToSensor(SensorDTO sensorDTO) {
        return modelMapper.map(sensorDTO, Sensor.class);
    }
}
