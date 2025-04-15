import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Home from './pages/Home';
import Product from './pages/Products';
import FilteredReceipts from './components/FilteredReceipts';
import Header from './components/Header'; // <-- Pievienots
import AddReceipt from './pages/AddReceipt';
import Category from './pages/CategoryStats';
import ManualEntry from './pages/ManualEntry';

function App() {
  return (
    <Router>
      <Header />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/add" element={<AddReceipt />} />
        <Route path="/products" element={<Product />} />
        <Route path="/filter" element={<FilteredReceipts />} />
        <Route path="/receipts" element={<Home />} />
        <Route path="/categories" element={<Category />} />
        <Route path="/manual" element={<ManualEntry />} />
      </Routes>
    </Router>
  );
}

export default App;
