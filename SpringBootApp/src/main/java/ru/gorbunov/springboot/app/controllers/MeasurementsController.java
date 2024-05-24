package ru.gorbunov.springboot.app.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.gorbunov.springboot.app.dto.MeasurementDTO;
import ru.gorbunov.springboot.app.dto.MeasurementsResponse;
import ru.gorbunov.springboot.app.models.Measurement;
import ru.gorbunov.springboot.app.services.MeasurementService;
import ru.gorbunov.springboot.app.util.MeasurementErrorResponse;
import ru.gorbunov.springboot.app.util.MeasurementException;
import ru.gorbunov.springboot.app.util.MeasurementValidator;

import javax.validation.Valid;
import java.util.stream.Collectors;

import static ru.gorbunov.springboot.app.util.ErrorsUtil.returnErrorsToClient;

@RestController
@RequestMapping("/measurements")
@Tag(name = "Контроллер измерений")

public class MeasurementsController {


    private final MeasurementService measurementService;
    private final MeasurementValidator measurementValidator;
    private final ModelMapper modelMapper;

    @Autowired
    public MeasurementsController(MeasurementService measurementService,
                                  MeasurementValidator measurementValidator,
                                  ModelMapper modelMapper) {
        this.measurementService = measurementService;
        this.measurementValidator = measurementValidator;
        this.modelMapper = modelMapper;
    }

    @Operation(
            summary = "Добавление измерений",
            description = "Позволяет добавить измерения"
    )
    @PostMapping("/add")
    public ResponseEntity<HttpStatus> add(@RequestBody @Valid MeasurementDTO measurementDTO,
                               BindingResult bindingResult) {
        Measurement measurementToAdd = convertToMeasurement(measurementDTO);

        measurementValidator.validate(measurementToAdd, bindingResult);
        if (bindingResult.hasErrors())
            returnErrorsToClient(bindingResult);

        measurementService.addMeasurement(measurementToAdd);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Operation(
            summary = "Получить все измерения"
    )
    @GetMapping()
    public MeasurementsResponse getMeasurements() {
        return new MeasurementsResponse(measurementService.findAll().stream().map(this::convertToMeasurementDTO)
                .collect(Collectors.toList()));
    }

    @Operation(
            summary = "Информация о количестве дождливых дней",
            description = "Позволяет получить информацию о количестве дождливых дней"
    )
    @GetMapping("/rainyDaysCount")
    public Long getRainyDaysCount() {
        return measurementService.findAll().stream().filter(Measurement::isRaining).count();
    }

    private Measurement convertToMeasurement(MeasurementDTO measurementDTO) {
        return modelMapper.map(measurementDTO, Measurement.class);
    }

    private MeasurementDTO convertToMeasurementDTO(Measurement measurement) {
        return modelMapper.map(measurement, MeasurementDTO.class);
    }

    @ExceptionHandler
    private ResponseEntity<MeasurementErrorResponse> handleException(MeasurementException e) {
        MeasurementErrorResponse response = new MeasurementErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
