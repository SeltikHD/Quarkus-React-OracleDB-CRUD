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

import type { IProduct, IProductRequest } from '@/types';

interface IEditProductModalProps {
  open: boolean;
  product: IProduct;
  onClose: () => void;
  onSubmit: (id: number, data: IProductRequest) => void;
}

/**
 * Modal dialog for editing an existing product.
 * Parent must conditionally render this component (with key={product.id})
 * so the form resets when the product changes.
 */
function EditProductModal({
  open,
  product,
  onClose,
  onSubmit,
}: Readonly<IEditProductModalProps>): ReactElement {
  const [form, setForm] = useState<IProductRequest>({
    name: product.name,
    description: product.description ?? '',
    sku: product.sku,
    unitPrice: product.unitPrice,
    stockQuantity: product.stockQuantity,
  });

  const handleChange = (field: keyof IProductRequest, value: string | number): void => {
    setForm((prev) => ({ ...prev, [field]: value }));
  };

  const handleSubmit = (): void => {
    onSubmit(product.id, form);
  };

  const isValid = form.name.trim().length > 0 && form.sku.trim().length > 0 && form.unitPrice > 0;

  return (
    <Dialog fullWidth maxWidth="sm" open={open} onClose={onClose}>
      <DialogTitle>Edit Product</DialogTitle>
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

export default EditProductModal;
