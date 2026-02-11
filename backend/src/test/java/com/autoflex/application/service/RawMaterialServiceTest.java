package com.autoflex.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.autoflex.domain.model.rawmaterial.MeasurementUnit;
import com.autoflex.domain.model.rawmaterial.RawMaterial;
import com.autoflex.domain.model.rawmaterial.RawMaterialId;
import com.autoflex.domain.port.in.RawMaterialUseCase.CreateRawMaterialCommand;
import com.autoflex.domain.port.in.RawMaterialUseCase.RawMaterialCodeAlreadyExistsException;
import com.autoflex.domain.port.in.RawMaterialUseCase.RawMaterialNotFoundException;
import com.autoflex.domain.port.out.RawMaterialRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("RawMaterialService")
@ExtendWith(MockitoExtension.class)
class RawMaterialServiceTest {

  @Mock private RawMaterialRepository rawMaterialRepository;

  @InjectMocks private RawMaterialService rawMaterialService;

  @Nested
  @DisplayName("When creating a raw material")
  class CreateRawMaterial {

    @Test
    @DisplayName("should create raw material with valid data")
    void shouldCreateRawMaterial() {
      var command =
          new CreateRawMaterialCommand(
              "Steel Sheet",
              "High-grade steel",
              "RM-STEEL-001",
              MeasurementUnit.KILOGRAM,
              new BigDecimal("500"),
              new BigDecimal("15.50"));

      when(rawMaterialRepository.existsByCode("RM-STEEL-001")).thenReturn(false);
      when(rawMaterialRepository.save(any(RawMaterial.class)))
          .thenAnswer(
              inv -> {
                RawMaterial rm = inv.getArgument(0);
                return RawMaterial.reconstitute(
                    RawMaterialId.of(1L),
                    rm.getName(),
                    rm.getDescription(),
                    rm.getCode(),
                    rm.getUnit(),
                    rm.getStockQuantity(),
                    rm.getUnitCost(),
                    rm.isActive(),
                    rm.getCreatedAt(),
                    rm.getUpdatedAt());
              });

      RawMaterial result = rawMaterialService.createRawMaterial(command);

      assertThat(result.getName()).isEqualTo("Steel Sheet");
      assertThat(result.getId()).isNotNull();
      verify(rawMaterialRepository).save(any(RawMaterial.class));
    }

    @Test
    @DisplayName("should reject duplicate code")
    void shouldRejectDuplicateCode() {
      var command =
          new CreateRawMaterialCommand(
              "Steel", null, "RM-001", MeasurementUnit.KILOGRAM, BigDecimal.ZERO, BigDecimal.TEN);
      when(rawMaterialRepository.existsByCode("RM-001")).thenReturn(true);

      assertThatThrownBy(() -> rawMaterialService.createRawMaterial(command))
          .isInstanceOf(RawMaterialCodeAlreadyExistsException.class);
    }
  }

  @Nested
  @DisplayName("When adjusting stock")
  class AdjustStock {

    @Test
    @DisplayName("should adjust stock successfully")
    void shouldAdjustStock() {
      RawMaterial existing =
          RawMaterial.create(
              "Steel",
              null,
              "RM-001",
              MeasurementUnit.KILOGRAM,
              new BigDecimal("100"),
              BigDecimal.TEN);

      when(rawMaterialRepository.findById(any())).thenReturn(Optional.of(existing));
      when(rawMaterialRepository.save(any(RawMaterial.class)))
          .thenAnswer(inv -> inv.getArgument(0));

      RawMaterial result =
          rawMaterialService.adjustStock(RawMaterialId.of(1L), new BigDecimal("50"));

      assertThat(result.getStockQuantity()).isEqualByComparingTo(new BigDecimal("150"));
    }

    @Test
    @DisplayName("should throw when raw material not found")
    void shouldThrowWhenNotFound() {
      when(rawMaterialRepository.findById(any())).thenReturn(Optional.empty());

      assertThatThrownBy(
              () -> rawMaterialService.adjustStock(RawMaterialId.of(99L), BigDecimal.TEN))
          .isInstanceOf(RawMaterialNotFoundException.class);
    }
  }

  @Nested
  @DisplayName("When deactivating")
  class Deactivate {

    @Test
    @DisplayName("should deactivate raw material")
    void shouldDeactivate() {
      RawMaterial existing =
          RawMaterial.create(
              "Steel", null, "RM-001", MeasurementUnit.KILOGRAM, BigDecimal.ZERO, BigDecimal.TEN);

      when(rawMaterialRepository.findById(any())).thenReturn(Optional.of(existing));
      when(rawMaterialRepository.save(any(RawMaterial.class)))
          .thenAnswer(inv -> inv.getArgument(0));

      rawMaterialService.deactivateRawMaterial(RawMaterialId.of(1L));

      assertThat(existing.isActive()).isFalse();
      verify(rawMaterialRepository).save(existing);
    }
  }
}
