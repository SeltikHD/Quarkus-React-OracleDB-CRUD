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

import type { IRawMaterialRequest, MeasurementUnit } from '@/types';

interface IAddRawMaterialModalProps {
  open: boolean;
  onClose: () => void;
  onSubmit: (data: IRawMaterialRequest) => void;
}

const INITIAL_FORM: IRawMaterialRequest = {
  name: '',
  description: '',
  code: '',
  unit: 'UNIT',
  unitCost: 0,
  stockQuantity: 0,
};

function AddRawMaterialModal({
  open,
  onClose,
  onSubmit,
}: Readonly<IAddRawMaterialModalProps>): ReactElement {
  const [form, setForm] = useState<IRawMaterialRequest>(INITIAL_FORM);

  const handleChange = (field: keyof IRawMaterialRequest, value: string | number): void => {
    setForm((prev) => ({ ...prev, [field]: value }));
  };

  const handleSubmit = (): void => {
    onSubmit(form);
    setForm(INITIAL_FORM);
  };

  const isValid = form.name.trim().length > 0 && form.code.trim().length > 0 && form.unitCost > 0;

  return (
    <Dialog fullWidth maxWidth="sm" open={open} onClose={onClose}>
      <DialogTitle>Add Raw Material</DialogTitle>
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
          placeholder="RM-STEEL-001"
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
        <TextField
          fullWidth
          inputProps={{ min: 0, step: '0.01' }}
          label="Initial Stock Quantity"
          type="number"
          value={form.stockQuantity ?? 0}
          onChange={(e) => {
            handleChange('stockQuantity', Number.parseFloat(e.target.value) || 0);
          }}
        />
      </DialogContent>
      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button onClick={onClose}>Cancel</Button>
        <Button disabled={!isValid} variant="contained" onClick={handleSubmit}>
          Create
        </Button>
      </DialogActions>
    </Dialog>
  );
}

export default AddRawMaterialModal;
