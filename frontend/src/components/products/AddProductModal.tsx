import { useState } from 'react';

import type { ReactElement } from 'react';

import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
} from '@mui/material';

import type { IProductRequest } from '@/types';

interface IAddProductModalProps {
  open: boolean;
  onClose: () => void;
  onSubmit: (data: IProductRequest) => void;
}

const INITIAL_FORM: IProductRequest = {
  name: '',
  description: '',
  sku: '',
  unitPrice: 0,
  stockQuantity: 0,
};

function AddProductModal({
  open,
  onClose,
  onSubmit,
}: Readonly<IAddProductModalProps>): ReactElement {
  const [form, setForm] = useState<IProductRequest>(INITIAL_FORM);

  const handleChange = (field: keyof IProductRequest, value: string | number): void => {
    setForm((prev) => ({ ...prev, [field]: value }));
  };

  const handleSubmit = (): void => {
    onSubmit(form);
    setForm(INITIAL_FORM);
  };

  const isValid = form.name.trim().length > 0 && form.sku.trim().length > 0 && form.unitPrice > 0;

  return (
    <Dialog fullWidth maxWidth="sm" open={open} onClose={onClose}>
      <DialogTitle>Add Product</DialogTitle>
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
          label="SKU"
          placeholder="COMP-XYZ-001"
          required
          value={form.sku}
          onChange={(e) => {
            handleChange('sku', e.target.value.toUpperCase());
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
          inputProps={{ min: 0, step: '0.01' }}
          label="Unit Price"
          required
          type="number"
          value={form.unitPrice}
          onChange={(e) => {
            handleChange('unitPrice', Number.parseFloat(e.target.value) || 0);
          }}
        />
        <TextField
          fullWidth
          inputProps={{ min: 0, step: 1 }}
          label="Initial Stock Quantity"
          type="number"
          value={form.stockQuantity ?? 0}
          onChange={(e) => {
            handleChange('stockQuantity', Number.parseInt(e.target.value, 10) || 0);
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

export default AddProductModal;
