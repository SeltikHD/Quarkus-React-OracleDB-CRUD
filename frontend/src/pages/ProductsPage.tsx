import { useCallback, useEffect, useState } from 'react';

import type { ReactElement } from 'react';

import AddIcon from '@mui/icons-material/Add';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import EditIcon from '@mui/icons-material/Edit';
import ListAltIcon from '@mui/icons-material/ListAlt';
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
import AddProductModal from '@/components/products/AddProductModal';
import EditProductModal from '@/components/products/EditProductModal';
import ManageBomDrawer from '@/components/products/ManageBomDrawer';
import {
  useAppDispatch,
  useAppSelector,
  fetchProducts,
  createProduct,
  updateProduct,
  deleteProduct,
} from '@/store';

import type { IProduct, IProductRequest } from '@/types';

function ProductsPage(): ReactElement {
  const dispatch = useAppDispatch();
  const { items, loading, error } = useAppSelector((state) => state.products);
  const { notification, showSuccess, showError, closeNotification } = useNotification();

  const [addModalOpen, setAddModalOpen] = useState(false);
  const [editModal, setEditModal] = useState<{
    open: boolean;
    product: IProduct | null;
  }>({ open: false, product: null });
  const [deleteDialog, setDeleteDialog] = useState<{
    open: boolean;
    product: IProduct | null;
  }>({ open: false, product: null });
  const [bomDrawer, setBomDrawer] = useState<{
    open: boolean;
    product: IProduct | null;
  }>({ open: false, product: null });

  useEffect(() => {
    void dispatch(fetchProducts());
  }, [dispatch]);

  useEffect(() => {
    if (error !== null) {
      showError(error);
    }
  }, [error, showError]);

  // Keep the BOM drawer's product reference in sync with Redux state
  const currentBomProduct =
    bomDrawer.product === null
      ? null
      : (items.find((p) => p.id === bomDrawer.product?.id) ?? bomDrawer.product);

  const handleCreate = useCallback(
    (data: IProductRequest): void => {
      void dispatch(createProduct(data))
        .unwrap()
        .then(() => {
          showSuccess('Product created successfully');
          setAddModalOpen(false);
        })
        .catch((err: string) => {
          showError(err);
        });
    },
    [dispatch, showSuccess, showError]
  );

  const handleUpdate = useCallback(
    (id: number, data: IProductRequest): void => {
      void dispatch(updateProduct({ id, product: data }))
        .unwrap()
        .then(() => {
          showSuccess('Product updated successfully');
          setEditModal({ open: false, product: null });
        })
        .catch((err: string) => {
          showError(err);
        });
    },
    [dispatch, showSuccess, showError]
  );

  const handleDelete = useCallback((): void => {
    if (deleteDialog.product === null) {
      return;
    }
    void dispatch(deleteProduct(deleteDialog.product.id))
      .unwrap()
      .then(() => {
        showSuccess('Product deleted successfully');
        setDeleteDialog({ open: false, product: null });
      })
      .catch((err: string) => {
        showError(err);
        setDeleteDialog({ open: false, product: null });
      });
  }, [dispatch, deleteDialog.product, showSuccess, showError]);

  const formatCurrency = (value: number): string =>
    new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(value);

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography component="h2" variant="h5">
          Products
        </Typography>
        <Button
          data-testid="add-product-btn"
          startIcon={<AddIcon />}
          variant="contained"
          onClick={() => {
            setAddModalOpen(true);
          }}
        >
          Add Product
        </Button>
      </Box>

      {loading && items.length === 0 ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
          <CircularProgress />
        </Box>
      ) : (
        <TableContainer component={Paper} data-testid="products-table">
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>ID</TableCell>
                <TableCell>Name</TableCell>
                <TableCell>SKU</TableCell>
                <TableCell align="right">Unit Price</TableCell>
                <TableCell align="right">Stock</TableCell>
                <TableCell align="center">Materials</TableCell>
                <TableCell align="center">Status</TableCell>
                <TableCell align="center">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {items.length === 0 ? (
                <TableRow>
                  <TableCell align="center" colSpan={8}>
                    <Typography color="text.secondary" sx={{ py: 4 }} variant="body1">
                      No products found. Click &quot;Add Product&quot; to create one.
                    </Typography>
                  </TableCell>
                </TableRow>
              ) : (
                items.map((product) => (
                  <TableRow key={product.id} data-testid={`product-row-${product.id}`} hover>
                    <TableCell>{product.id}</TableCell>
                    <TableCell data-testid="product-name">{product.name}</TableCell>
                    <TableCell>
                      <Chip label={product.sku} size="small" variant="outlined" />
                    </TableCell>
                    <TableCell align="right">{formatCurrency(product.unitPrice)}</TableCell>
                    <TableCell align="right">{product.stockQuantity}</TableCell>
                    <TableCell align="center">
                      <Chip
                        color={product.materials.length > 0 ? 'info' : 'default'}
                        label={`${String(product.materials.length)} items`}
                        size="small"
                      />
                    </TableCell>
                    <TableCell align="center">
                      <Chip
                        color={product.active ? 'success' : 'default'}
                        label={product.active ? 'Active' : 'Inactive'}
                        size="small"
                      />
                    </TableCell>
                    <TableCell align="center">
                      <Box sx={{ display: 'flex', justifyContent: 'center', gap: 0.5 }}>
                        <Tooltip title="Edit">
                          <IconButton
                            color="info"
                            data-testid="edit-product-btn"
                            size="small"
                            onClick={() => {
                              setEditModal({ open: true, product });
                            }}
                          >
                            <EditIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                        <Tooltip title="Manage BOM">
                          <IconButton
                            color="primary"
                            data-testid="manage-bom-btn"
                            size="small"
                            onClick={() => {
                              setBomDrawer({ open: true, product });
                            }}
                          >
                            <ListAltIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                        <Tooltip title="Delete">
                          <IconButton
                            color="error"
                            data-testid="delete-product-btn"
                            size="small"
                            onClick={() => {
                              setDeleteDialog({ open: true, product });
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

      <AddProductModal
        open={addModalOpen}
        onClose={() => {
          setAddModalOpen(false);
        }}
        onSubmit={handleCreate}
      />

      {editModal.product !== null && (
        <EditProductModal
          key={editModal.product.id}
          open={editModal.open}
          product={editModal.product}
          onClose={() => {
            setEditModal({ open: false, product: null });
          }}
          onSubmit={handleUpdate}
        />
      )}

      {deleteDialog.product !== null && (
        <ConfirmDeleteDialog
          message={`Are you sure you want to delete "${deleteDialog.product.name}" (${deleteDialog.product.sku})? This is a soft-delete and can be reversed.`}
          open={deleteDialog.open}
          title="Delete Product"
          onClose={() => {
            setDeleteDialog({ open: false, product: null });
          }}
          onConfirm={handleDelete}
        />
      )}

      <ManageBomDrawer
        open={bomDrawer.open}
        product={currentBomProduct}
        onClose={() => {
          setBomDrawer({ open: false, product: null });
        }}
        onError={showError}
        onSuccess={showSuccess}
      />

      <NotificationSnackbar notification={notification} onClose={closeNotification} />
    </Box>
  );
}

export default ProductsPage;
