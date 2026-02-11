import { useState } from 'react';

import type { ReactElement } from 'react';

import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
  Typography,
} from '@mui/material';

interface IAdjustStockDialogProps {
  open: boolean;
  materialName: string;
  currentStock: number;
  onClose: () => void;
  onSubmit: (quantity: number) => void;
}

function AdjustStockDialog({
  open,
  materialName,
  currentStock,
  onClose,
  onSubmit,
}: Readonly<IAdjustStockDialogProps>): ReactElement {
  const [quantity, setQuantity] = useState<string>('0');

  const numericValue = Number.parseFloat(quantity) || 0;

  const handleSubmit = (): void => {
    onSubmit(numericValue);
    setQuantity('0');
  };

  return (
    <Dialog fullWidth maxWidth="xs" open={open} onClose={onClose}>
      <DialogTitle>Adjust Stock</DialogTitle>
      <DialogContent
        sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: '16px !important' }}
      >
        <Typography color="text.secondary" variant="body2">
          Material: <strong>{materialName}</strong>
        </Typography>
        <Typography color="text.secondary" variant="body2">
          Current Stock: <strong>{currentStock}</strong>
        </Typography>
        <TextField
          autoFocus
          fullWidth
          helperText="Use positive to add, negative to subtract"
          inputProps={{ step: '0.01' }}
          label="Quantity to Adjust"
          type="number"
          value={quantity}
          onChange={(e) => {
            setQuantity(e.target.value);
          }}
        />
        {numericValue !== 0 && (
          <Typography color={numericValue > 0 ? 'success.main' : 'error.main'} variant="body2">
            New stock: {currentStock + numericValue}
          </Typography>
        )}
      </DialogContent>
      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button onClick={onClose}>Cancel</Button>
        <Button disabled={numericValue === 0} variant="contained" onClick={handleSubmit}>
          Adjust
        </Button>
      </DialogActions>
    </Dialog>
  );
}

export default AdjustStockDialog;
