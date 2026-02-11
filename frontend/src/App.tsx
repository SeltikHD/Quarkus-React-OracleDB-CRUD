import type { ReactElement } from 'react';

import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';

import Layout from '@/components/layout/Layout';
import DashboardPage from '@/pages/DashboardPage';
import ProductionPage from '@/pages/ProductionPage';
import ProductsPage from '@/pages/ProductsPage';
import RawMaterialsPage from '@/pages/RawMaterialsPage';

/**
 * Main Application Component.
 *
 * Sets up routing with a persistent sidebar/navbar layout.
 */
function App(): ReactElement {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<Layout />} path="/">
          <Route element={<DashboardPage />} index />
          <Route element={<RawMaterialsPage />} path="raw-materials" />
          <Route element={<ProductsPage />} path="products" />
          <Route element={<ProductionPage />} path="production" />
          <Route element={<Navigate replace to="/" />} path="*" />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
