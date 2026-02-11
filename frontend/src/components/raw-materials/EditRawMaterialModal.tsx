import { useState } from 'react';

import type { ReactElement } from 'react';

import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  MenuItem,
  TextField,
} from '@mui/material';

import { MEASUREMENT_UNITS } from '@/types';

import type { IRawMaterial, IRawMaterialRequest, MeasurementUnit } from '@/types';

interface IEditRawMaterialModalProps {
  open: boolean;
  material: IRawMaterial;
  onClose: () => void;
  onSubmit: (id: number, data: IRawMaterialRequest) => void;
}

/**
 * Modal dialog for editing an existing raw material.
 * Parent must conditionally render this component (with key={material.id})
 * so the form resets when the material changes.
 */
function EditRawMaterialModal({
  open,
  material,
  onClose,
  onSubmit,
}: Readonly<IEditRawMaterialModalProps>): ReactElement {
  const [form, setForm] = useState<IRawMaterialRequest>({
    name: material.name,
    description: material.description ?? '',
    code: material.code,
    unit: material.unit,
    unitCost: material.unitCost,
    stockQuantity: material.stockQuantity,
  });

  const handleChange = (field: keyof IRawMaterialRequest, value: string | number): void => {
    setForm((prev) => ({ ...prev, [field]: value }));
  };

  const handleSubmit = (): void => {
    onSubmit(material.id, form);
  };

  const isValid = form.name.trim().length > 0 && form.code.trim().length > 0 && form.unitCost > 0;

  return (
    <Dialog fullWidth maxWidth="sm" open={open} onClose={onClose}>
      <DialogTitle>Edit Raw Material</DialogTitle>
      <DialogContent
        sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: '16px !important' }}
      >
        <TextField
          autoFocus
          required
          fullWidth
          label="Name"
          value={form.name}
          onChange={(e) => {
            handleChange('name', e.target.value);
          }}
        />
        <TextField
          fullWidth
          label="Code"
          required
          value={form.code}
          onChange={(e) => {
            handleChange('code', e.target.value.toUpperCase());
          }}
        />
        <TextField
          fullWidth
          label="Description"
          multiline
          rows={2}
          value={form.description ?? ''}
          onChange={(e) => {
            handleChange('description', e.target.value);
          }}
        />
        <TextField
          fullWidth
          label="Unit"
          required
          select
          value={form.unit}
          onChange={(e) => {
            handleChange('unit', e.target.value as MeasurementUnit);
          }}
        >
          {MEASUREMENT_UNITS.map((unit) => (
            <MenuItem key={unit} value={unit}>
              {unit}
            </MenuItem>
          ))}
        </TextField>
        <TextField
          fullWidth
          inputProps={{ min: 0, step: '0.01' }}
          label="Unit Cost"
          required
          type="number"
          value={form.unitCost}
          onChange={(e) => {
            handleChange('unitCost', Number.parseFloat(e.target.value) || 0);
          }}
        />
      </DialogContent>
      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button onClick={onClose}>Cancel</Button>
        <Button disabled={!isValid} variant="contained" onClick={handleSubmit}>
          Save Changes
        </Button>
      </DialogActions>
    </Dialog>
  );
}

export default EditRawMaterialModal;
