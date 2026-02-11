import { useCallback, useEffect, useState } from 'react';

import type { ReactElement } from 'react';

import AddIcon from '@mui/icons-material/Add';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import EditIcon from '@mui/icons-material/Edit';
import InventoryIcon from '@mui/icons-material/Inventory';
import {
  Box,
  Button,
  Chip,
  CircularProgress,
  IconButton,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Tooltip,
  Typography,
} from '@mui/material';

import ConfirmDeleteDialog from '@/components/common/ConfirmDeleteDialog';
import NotificationSnackbar, { useNotification } from '@/components/common/NotificationSnackbar';
import AddRawMaterialModal from '@/components/raw-materials/AddRawMaterialModal';
import AdjustStockDialog from '@/components/raw-materials/AdjustStockDialog';
import EditRawMaterialModal from '@/components/raw-materials/EditRawMaterialModal';
import {
  useAppDispatch,
  useAppSelector,
  fetchRawMaterials,
  createRawMaterial,
  updateRawMaterial,
  adjustRawMaterialStock,
  deleteRawMaterial,
} from '@/store';

import type { IRawMaterial, IRawMaterialRequest } from '@/types';

function RawMaterialsPage(): ReactElement {
  const dispatch = useAppDispatch();
  const { items, loading, error } = useAppSelector((state) => state.rawMaterials);
  const { notification, showSuccess, showError, closeNotification } = useNotification();

  const [addModalOpen, setAddModalOpen] = useState(false);
  const [editModal, setEditModal] = useState<{
    open: boolean;
    material: IRawMaterial | null;
  }>({ open: false, material: null });
  const [stockDialog, setStockDialog] = useState<{
    open: boolean;
    material: IRawMaterial | null;
  }>({ open: false, material: null });
  const [deleteDialog, setDeleteDialog] = useState<{
    open: boolean;
    material: IRawMaterial | null;
  }>({ open: false, material: null });

  useEffect(() => {
    void dispatch(fetchRawMaterials());
  }, [dispatch]);

  useEffect(() => {
    if (error !== null) {
      showError(error);
    }
  }, [error, showError]);

  const handleCreate = useCallback(
    (data: IRawMaterialRequest): void => {
      void dispatch(createRawMaterial(data))
        .unwrap()
        .then(() => {
          showSuccess('Raw material created successfully');
          setAddModalOpen(false);
        })
        .catch((err: string) => {
          showError(err);
        });
    },
    [dispatch, showSuccess, showError]
  );

  const handleUpdate = useCallback(
    (id: number, data: IRawMaterialRequest): void => {
      void dispatch(updateRawMaterial({ id, material: data }))
        .unwrap()
        .then(() => {
          showSuccess('Raw material updated successfully');
          setEditModal({ open: false, material: null });
        })
        .catch((err: string) => {
          showError(err);
        });
    },
    [dispatch, showSuccess, showError]
  );

  const handleDelete = useCallback((): void => {
    if (deleteDialog.material === null) {
      return;
    }
    void dispatch(deleteRawMaterial(deleteDialog.material.id))
      .unwrap()
      .then(() => {
        showSuccess('Raw material deleted successfully');
        setDeleteDialog({ open: false, material: null });
      })
      .catch((err: string) => {
        showError(err);
        setDeleteDialog({ open: false, material: null });
      });
  }, [dispatch, deleteDialog.material, showSuccess, showError]);

  const handleAdjustStock = useCallback(
    (quantity: number): void => {
      if (stockDialog.material === null) {
        return;
      }
      void dispatch(adjustRawMaterialStock({ id: stockDialog.material.id, quantity }))
        .unwrap()
        .then(() => {
          showSuccess('Stock adjusted successfully');
          setStockDialog({ open: false, material: null });
        })
        .catch((err: string) => {
          showError(err);
        });
    },
    [dispatch, stockDialog.material, showSuccess, showError]
  );

  const formatCurrency = (value: number): string =>
    new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(value);

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography component="h2" variant="h5">
          Raw Materials
        </Typography>
        <Button
          data-testid="add-material-btn"
          startIcon={<AddIcon />}
          variant="contained"
          onClick={() => {
            setAddModalOpen(true);
          }}
        >
          Add Material
        </Button>
      </Box>

      {loading && items.length === 0 ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
          <CircularProgress />
        </Box>
      ) : (
        <TableContainer component={Paper} data-testid="raw-materials-table">
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>ID</TableCell>
                <TableCell>Name</TableCell>
                <TableCell>Code</TableCell>
                <TableCell>Unit</TableCell>
                <TableCell align="right">Stock</TableCell>
                <TableCell align="right">Unit Cost</TableCell>
                <TableCell align="center">Status</TableCell>
                <TableCell align="center">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {items.length === 0 ? (
                <TableRow>
                  <TableCell align="center" colSpan={8}>
                    <Typography color="text.secondary" sx={{ py: 4 }} variant="body1">
                      No raw materials found. Click &quot;Add Material&quot; to create one.
                    </Typography>
                  </TableCell>
                </TableRow>
              ) : (
                items.map((material) => (
                  <TableRow key={material.id} data-testid={`material-row-${material.id}`} hover>
                    <TableCell>{material.id}</TableCell>
                    <TableCell data-testid="material-name">{material.name}</TableCell>
                    <TableCell>
                      <Chip label={material.code} size="small" variant="outlined" />
                    </TableCell>
                    <TableCell>{material.unitAbbreviation}</TableCell>
                    <TableCell align="right" data-testid="material-stock">
                      {material.stockQuantity}
                    </TableCell>
                    <TableCell align="right">{formatCurrency(material.unitCost)}</TableCell>
                    <TableCell align="center">
                      <Chip
                        color={material.active ? 'success' : 'default'}
                        label={material.active ? 'Active' : 'Inactive'}
                        size="small"
                      />
                    </TableCell>
                    <TableCell align="center">
                      <Box sx={{ display: 'flex', justifyContent: 'center', gap: 0.5 }}>
                        <Tooltip title="Edit">
                          <IconButton
                            color="info"
                            data-testid="edit-material-btn"
                            size="small"
                            onClick={() => {
                              setEditModal({ open: true, material });
                            }}
                          >
                            <EditIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                        <Tooltip title="Adjust Stock">
                          <IconButton
                            color="primary"
                            data-testid="adjust-stock-btn"
                            size="small"
                            onClick={() => {
                              setStockDialog({ open: true, material });
                            }}
                          >
                            <InventoryIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                        <Tooltip title="Delete">
                          <IconButton
                            color="error"
                            data-testid="delete-material-btn"
                            size="small"
                            onClick={() => {
                              setDeleteDialog({ open: true, material });
                            }}
                          >
                            <DeleteOutlineIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                      </Box>
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      <AddRawMaterialModal
        open={addModalOpen}
        onClose={() => {
          setAddModalOpen(false);
        }}
        onSubmit={handleCreate}
      />

      {editModal.material !== null && (
        <EditRawMaterialModal
          key={editModal.material.id}
          material={editModal.material}
          open={editModal.open}
          onClose={() => {
            setEditModal({ open: false, material: null });
          }}
          onSubmit={handleUpdate}
        />
      )}

      {stockDialog.material !== null && (
        <AdjustStockDialog
          currentStock={stockDialog.material.stockQuantity}
          materialName={stockDialog.material.name}
          open={stockDialog.open}
          onClose={() => {
            setStockDialog({ open: false, material: null });
          }}
          onSubmit={handleAdjustStock}
        />
      )}

      {deleteDialog.material !== null && (
        <ConfirmDeleteDialog
          message={`Are you sure you want to delete "${deleteDialog.material.name}"? This action may fail if the material is used in a product's bill of materials.`}
          open={deleteDialog.open}
          title="Delete Raw Material"
          onClose={() => {
            setDeleteDialog({ open: false, material: null });
          }}
          onConfirm={handleDelete}
        />
      )}

      <NotificationSnackbar notification={notification} onClose={closeNotification} />
    </Box>
  );
}

export default RawMaterialsPage;
