import React from 'react';
import { Link, useLocation } from 'react-router-dom';

function Header() {
  const location = useLocation();

  const linkClasses = (path) =>
    `px-4 py-2 rounded-md transition-all duration-200 ${
      location.pathname === path
        ? 'bg-white text-blue-700 font-semibold shadow-sm'
        : 'text-white hover:bg-blue-600'
    }`;

  return (
    <header className="bg-white shadow">
      <div className="max-w-6xl mx-auto px-4 py-4 flex items-center justify-between">
        <h1 className="text-xl font-bold text-blue-700 flex items-center gap-2">
          💰 Budžeta Lietotne
        </h1>
        <nav className="flex gap-4 text-gray-700 font-medium">
          <Link to="/categories" className="hover:text-blue-600 transition">📂 Kategorijas</Link>
          <Link to="/receipts" className="hover:text-blue-600 transition">📄 Visi čeki</Link>
          <Link to="/products" className="hover:text-blue-600 transition">🛒 Produkti</Link>
          <Link to="/add" className="hover:text-blue-600 transition">📥 Pievienot čeku/kategoriju</Link>
        </nav>
      </div>
    </header>
  );
}

export default Header;
