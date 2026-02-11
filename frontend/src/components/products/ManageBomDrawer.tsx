import { useCallback, useEffect, useState } from 'react';

import type { ReactElement } from 'react';

import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import {
  Box,
  Button,
  Divider,
  Drawer,
  IconButton,
  MenuItem,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
  Toolbar,
  Tooltip,
  Typography,
} from '@mui/material';

import {
  useAppDispatch,
  useAppSelector,
  addMaterialToProduct,
  removeMaterialFromProduct,
  fetchRawMaterials,
} from '@/store';

import type { IProduct } from '@/types';

interface IManageBomDrawerProps {
  open: boolean;
  product: IProduct | null;
  onClose: () => void;
  onSuccess: (message: string) => void;
  onError: (message: string) => void;
}

const DRAWER_WIDTH = 520;

function ManageBomDrawer({
  open,
  product,
  onClose,
  onSuccess,
  onError,
}: IManageBomDrawerProps): ReactElement {
  const dispatch = useAppDispatch();
  const rawMaterials = useAppSelector((state) => state.rawMaterials.items);

  const [selectedMaterialId, setSelectedMaterialId] = useState<string>('');
  const [quantity, setQuantity] = useState<string>('1');

  // Make sure raw materials are loaded for the dropdown
  useEffect(() => {
    if (open && rawMaterials.length === 0) {
      void dispatch(fetchRawMaterials());
    }
  }, [open, rawMaterials.length, dispatch]);

  // Determine which raw materials are already in the BOM to filter the dropdown
  const materialsInBom = product?.materials.map((m) => m.rawMaterialId) ?? [];
  const availableMaterials = rawMaterials.filter(
    (rm) => !materialsInBom.includes(rm.id) && rm.active
  );

  // Build a lookup map for raw material names
  const materialNameMap = new Map(rawMaterials.map((rm) => [rm.id, rm.name]));

  const handleAddMaterial = useCallback((): void => {
    if (product === null || selectedMaterialId === '') {
      return;
    }

    const rawMaterialId = parseInt(selectedMaterialId, 10);
    const quantityRequired = parseFloat(quantity) || 1;

    void dispatch(
      addMaterialToProduct({
        productId: product.id,
        request: { rawMaterialId, quantityRequired },
      })
    )
      .unwrap()
      .then(() => {
        onSuccess('Material added to BOM');
        setSelectedMaterialId('');
        setQuantity('1');
      })
      .catch((err: string) => {
        onError(err);
      });
  }, [dispatch, product, selectedMaterialId, quantity, onSuccess, onError]);

  const handleRemoveMaterial = useCallback(
    (rawMaterialId: number): void => {
      if (product === null) {
        return;
      }
      void dispatch(removeMaterialFromProduct({ productId: product.id, rawMaterialId }))
        .unwrap()
        .then(() => {
          onSuccess('Material removed from BOM');
        })
        .catch((err: string) => {
          onError(err);
        });
    },
    [dispatch, product, onSuccess, onError]
  );

  return (
    <Drawer
      anchor="right"
      open={open}
      sx={{ '& .MuiDrawer-paper': { width: DRAWER_WIDTH } }}
      onClose={onClose}
    >
      <Toolbar sx={{ px: 3 }}>
        <Typography sx={{ flexGrow: 1 }} variant="h6">
          Manage BOM
        </Typography>
        <Button onClick={onClose}>Close</Button>
      </Toolbar>
      <Divider />

      {product !== null && (
        <Box sx={{ p: 3, display: 'flex', flexDirection: 'column', gap: 3, height: '100%' }}>
          {/* Product Info */}
          <Box>
            <Typography color="text.secondary" variant="body2">
              Product
            </Typography>
            <Typography fontWeight={600} variant="subtitle1">
              {product.name} ({product.sku})
            </Typography>
          </Box>

          <Divider />

          {/* Add Material Form */}
          <Box>
            <Typography gutterBottom variant="subtitle2">
              Add Material
            </Typography>
            <Box sx={{ display: 'flex', gap: 1, alignItems: 'flex-start' }}>
              <TextField
                label="Raw Material"
                select
                size="small"
                sx={{ flex: 2 }}
                value={selectedMaterialId}
                onChange={(e) => {
                  setSelectedMaterialId(e.target.value);
                }}
              >
                {availableMaterials.length === 0 ? (
                  <MenuItem disabled value="">
                    No materials available
                  </MenuItem>
                ) : (
                  availableMaterials.map((rm) => (
                    <MenuItem key={rm.id} value={String(rm.id)}>
                      {rm.name} ({rm.code})
                    </MenuItem>
                  ))
                )}
              </TextField>
              <TextField
                inputProps={{ min: 0.0001, step: '0.01' }}
                label="Qty"
                size="small"
                sx={{ flex: 1 }}
                type="number"
                value={quantity}
                onChange={(e) => {
                  setQuantity(e.target.value);
                }}
              />
              <Tooltip title="Add to BOM">
                <span>
                  <IconButton
                    color="primary"
                    disabled={selectedMaterialId === ''}
                    onClick={handleAddMaterial}
                  >
                    <AddCircleOutlineIcon />
                  </IconButton>
                </span>
              </Tooltip>
            </Box>
          </Box>

          <Divider />

          {/* Current BOM Table */}
          <Box sx={{ flex: 1, overflow: 'auto' }}>
            <Typography gutterBottom variant="subtitle2">
              Current Materials ({product.materials.length})
            </Typography>
            {product.materials.length === 0 ? (
              <Typography
                color="text.secondary"
                sx={{ py: 2, textAlign: 'center' }}
                variant="body2"
              >
                No materials assigned yet.
              </Typography>
            ) : (
              <TableContainer>
                <Table size="small">
                  <TableHead>
                    <TableRow>
                      <TableCell>Material</TableCell>
                      <TableCell align="right">Qty Required</TableCell>
                      <TableCell align="center">Action</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {product.materials.map((bom) => (
                      <TableRow key={bom.rawMaterialId}>
                        <TableCell>
                          {materialNameMap.get(bom.rawMaterialId) ??
                            `Material #${String(bom.rawMaterialId)}`}
                        </TableCell>
                        <TableCell align="right">{bom.quantityRequired}</TableCell>
                        <TableCell align="center">
                          <Tooltip title="Remove material">
                            <IconButton
                              color="error"
                              size="small"
                              onClick={() => {
                                handleRemoveMaterial(bom.rawMaterialId);
                              }}
                            >
                              <DeleteOutlineIcon fontSize="small" />
                            </IconButton>
                          </Tooltip>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            )}
          </Box>
        </Box>
      )}
    </Drawer>
  );
}

export default ManageBomDrawer;
