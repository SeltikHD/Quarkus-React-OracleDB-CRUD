import { Box, Container, Typography } from '@mui/material';
import { BrowserRouter, Routes, Route } from 'react-router-dom';

/**
 * Main Application Component.
 *
 * This is the root component that sets up routing and the main layout.
 * Additional pages will be added here as the application grows.
 */
function App(): JSX.Element {
  return (
    <BrowserRouter>
      <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
        {/* Main Content */}
        <Container component="main" sx={{ flex: 1, py: 4 }}>
          <Routes>
            <Route
              element={
                <Box>
                  <Typography gutterBottom component="h1" variant="h4">
                    Welcome to Autoflex ERP
                  </Typography>
                  <Typography color="text.secondary" variant="body1">
                    Product and Raw Material Management System
                  </Typography>
                </Box>
              }
              path="/"
            />
            {/* Add more routes here as pages are developed */}
            {/* <Route path="/products" element={<ProductsPage />} /> */}
            {/* <Route path="/raw-materials" element={<RawMaterialsPage />} /> */}
          </Routes>
        </Container>
      </Box>
    </BrowserRouter>
  );
}

export default App;
