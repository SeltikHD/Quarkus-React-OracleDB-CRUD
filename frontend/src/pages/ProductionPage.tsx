import { useCallback, useEffect } from 'react';

import type { ReactElement } from 'react';

import CalculateIcon from '@mui/icons-material/Calculate';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  Chip,
  CircularProgress,
  Divider,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from '@mui/material';

import NotificationSnackbar, { useNotification } from '@/components/common/NotificationSnackbar';
import { useAppDispatch, useAppSelector, calculateProductionPlan, clearPlan } from '@/store';

function ProductionPage(): ReactElement {
  const dispatch = useAppDispatch();
  const { plan, loading, error } = useAppSelector((state) => state.production);
  const { notification, showError, closeNotification } = useNotification();

  useEffect(() => {
    if (error !== null) {
      showError(error);
    }
  }, [error, showError]);

  const handleCalculate = useCallback((): void => {
    void dispatch(calculateProductionPlan());
  }, [dispatch]);

  const handleClear = useCallback((): void => {
    dispatch(clearPlan());
  }, [dispatch]);

  const formatCurrency = (value: number): string =>
    new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(value);

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography component="h2" variant="h5">
          Production Planning
        </Typography>
        <Box sx={{ display: 'flex', gap: 1 }}>
          {plan !== null && (
            <Button color="inherit" variant="outlined" onClick={handleClear}>
              Clear
            </Button>
          )}
          <Button
            disabled={loading}
            startIcon={loading ? <CircularProgress size={20} /> : <CalculateIcon />}
            variant="contained"
            onClick={handleCalculate}
          >
            {loading ? 'Calculating...' : 'Calculate Production'}
          </Button>
        </Box>
      </Box>

      {plan === null && !loading && (
        <Card sx={{ textAlign: 'center', py: 8 }}>
          <CardContent>
            <TrendingUpIcon color="primary" sx={{ fontSize: 64, mb: 2, opacity: 0.6 }} />
            <Typography gutterBottom color="text.secondary" variant="h6">
              Ready to Plan Production
            </Typography>
            <Typography
              color="text.secondary"
              sx={{ mb: 3, maxWidth: 500, mx: 'auto' }}
              variant="body2"
            >
              The Greedy Algorithm will analyze your raw material stock and determine the optimal
              production plan, prioritizing products with the highest unit price.
            </Typography>
            <Button
              size="large"
              startIcon={<CalculateIcon />}
              variant="contained"
              onClick={handleCalculate}
            >
              Calculate Production Plan
            </Button>
          </CardContent>
        </Card>
      )}

      {plan !== null && (
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
          {/* Summary Cards */}
          <Box
            sx={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))',
              gap: 2,
            }}
          >
            <Card>
              <CardContent>
                <Typography color="text.secondary" variant="body2">
                  Total Production Value
                </Typography>
                <Typography color="primary" fontWeight={700} variant="h4">
                  {formatCurrency(plan.totalProductionValue)}
                </Typography>
              </CardContent>
            </Card>
            <Card>
              <CardContent>
                <Typography color="text.secondary" variant="body2">
                  Total Units to Produce
                </Typography>
                <Typography color="success.main" fontWeight={700} variant="h4">
                  {plan.totalUnits}
                </Typography>
              </CardContent>
            </Card>
            <Card>
              <CardContent>
                <Typography color="text.secondary" variant="body2">
                  Product Lines
                </Typography>
                <Typography fontWeight={700} variant="h4">
                  {plan.items.length}
                </Typography>
              </CardContent>
            </Card>
          </Box>

          {/* Production Items Table */}
          <Box>
            <Typography gutterBottom variant="h6">
              Production Plan
            </Typography>
            {plan.items.length === 0 ? (
              <Alert severity="warning">
                No products can be produced with the current raw material stock. Make sure products
                have BOM definitions and raw materials have sufficient stock.
              </Alert>
            ) : (
              <TableContainer component={Paper}>
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell>Product</TableCell>
                      <TableCell>SKU</TableCell>
                      <TableCell align="right">Units to Produce</TableCell>
                      <TableCell align="right">Unit Price</TableCell>
                      <TableCell align="right">Total Value</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {plan.items.map((item) => (
                      <TableRow key={item.productId} hover>
                        <TableCell>
                          <Typography fontWeight={500}>{item.productName}</Typography>
                        </TableCell>
                        <TableCell>
                          <Chip label={item.productSku} size="small" variant="outlined" />
                        </TableCell>
                        <TableCell align="right">
                          <Chip color="success" label={item.quantity} size="small" />
                        </TableCell>
                        <TableCell align="right">{formatCurrency(item.unitPrice)}</TableCell>
                        <TableCell align="right">
                          <Typography fontWeight={600}>
                            {formatCurrency(item.totalValue)}
                          </Typography>
                        </TableCell>
                      </TableRow>
                    ))}
                    {/* Total Row */}
                    <TableRow sx={{ bgcolor: 'action.hover' }}>
                      <TableCell colSpan={2}>
                        <Typography fontWeight={700}>TOTAL</Typography>
                      </TableCell>
                      <TableCell align="right">
                        <Typography fontWeight={700}>{plan.totalUnits}</Typography>
                      </TableCell>
                      <TableCell />
                      <TableCell align="right">
                        <Typography color="primary" fontWeight={700}>
                          {formatCurrency(plan.totalProductionValue)}
                        </Typography>
                      </TableCell>
                    </TableRow>
                  </TableBody>
                </Table>
              </TableContainer>
            )}
          </Box>

          {/* Remaining Stock */}
          {Object.keys(plan.remainingStock).length > 0 && (
            <Box>
              <Typography gutterBottom variant="h6">
                Remaining Raw Material Stock
              </Typography>
              <Divider sx={{ mb: 2 }} />
              <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                {Object.entries(plan.remainingStock).map(([name, qty]) => (
                  <Chip
                    key={name}
                    color={qty > 0 ? 'default' : 'error'}
                    label={`${name}: ${String(qty)}`}
                    variant="outlined"
                  />
                ))}
              </Box>
            </Box>
          )}
        </Box>
      )}

      <NotificationSnackbar notification={notification} onClose={closeNotification} />
    </Box>
  );
}

export default ProductionPage;
