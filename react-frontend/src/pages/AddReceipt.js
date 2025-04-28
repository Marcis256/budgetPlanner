import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

function AddReceipt() {
  const [file, setFile] = useState(null);
  const [message, setMessage] = useState('');
  const [categoryName, setCategoryName] = useState('');
  const [categoryMessage, setCategoryMessage] = useState('');
  const [categories, setCategories] = useState([]);
  const [importMessage, setImportMessage] = useState('');

  const navigate = useNavigate();

  const fetchCategories = async () => {
    try {
      const res = await axios.get('http://localhost:8080/api/categories');
      setCategories(res.data);
    } catch (err) {
      console.error('❌ Neizdevās ielādēt kategorijas:', err);
    }
  };

  useEffect(() => {
    fetchCategories();
  }, []);

  const handleFileChange = (e) => setFile(e.target.files[0]);

  const handleUpload = async () => {
    if (!file) return;
    const formData = new FormData();
    formData.append('file', file);

    try {
      await axios.post('http://localhost:8080/api/receipts/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      setMessage('✅ Čeks augšupielādēts veiksmīgi!');
      setFile(null);
    } catch (error) {
      const errorMsg = error.response?.data?.message || JSON.stringify(error.response?.data || '❌ Nezināma kļūda.');
      setMessage(`❌ Kļūda: ${errorMsg}`);
          console.error(error);
    }
  };

  const handleImportReceipts = async () => {
    try {
      const res = await axios.post('http://localhost:8080/api/receipts/import-folder');
      setImportMessage(`✅ ${res.data}`);
    } catch (err) {
      console.error(err);
      setImportMessage('❌ Neizdevās importēt PDF čekus.');
    }
  };

  const handleAddCategory = async () => {
    if (!categoryName.trim()) return;

    try {
      await axios.post('http://localhost:8080/api/categories', { name: categoryName });
      setCategoryMessage('✅ Kategorija pievienota!');
      setCategoryName('');
      fetchCategories();
    } catch (err) {
      console.error(err);
      setCategoryMessage('❌ Neizdevās pievienot kategoriju.');
    }
  };

const handleUpdateCategory = async (id, name) => {
  try {
    await axios.put(`http://localhost:8080/api/categories/${id}`, { name });
    setCategoryMessage('✅ Kategorija atjaunināta!');
    fetchCategories();
  } catch (err) {
    console.error(err);
    setCategoryMessage('❌ Neizdevās atjaunināt kategoriju.');
  }
};

const handleDeleteCategory = async (id) => {
  try {
    await axios.delete(`http://localhost:8080/api/categories/${id}`);
    setCategoryMessage('🗑️ Kategorija dzēsta!');
    fetchCategories();
  } catch (err) {
    const errorMsg = err.response?.data?.message || err.response?.data || 'Nezināma kļūda.';
    setCategoryMessage(`❌ ${errorMsg}`);
    console.error(err);
  }
};

  return (
    <div className="min-h-screen bg-gradient-to-tr from-blue-50 via-white to-green-50 p-6">
      <div className="max-w-4xl mx-auto">
        <h1 className="text-3xl font-bold mb-6 text-center">📤 Pievienot čeku</h1>

        {/* Čeka augšupielāde */}
        <div className="bg-white rounded-xl shadow-lg p-6 mb-10 border border-blue-200">
          <h2 className="text-xl font-semibold text-blue-800 mb-4">PDF Čeka augšupielāde</h2>
          <div className="flex flex-col sm:flex-row items-center gap-4">
            <input
              type="file"
              accept="application/pdf"
              onChange={handleFileChange}
              className="border border-gray-300 p-2 rounded w-full sm:w-auto"
            />
            <button
              onClick={handleUpload}
              className="bg-blue-600 text-white px-6 py-2 rounded hover:bg-blue-700 transition disabled:bg-gray-400"
              disabled={!file}
            >
              Augšupielādēt
            </button>
            <button
              onClick={handleImportReceipts}
              className="bg-indigo-600 text-white px-4 py-2 rounded hover:bg-indigo-700"
            >
              📥 Importēt PDF čekus
            </button>
            <button
              onClick={() => navigate('/manual')}
              className="bg-gray-600 text-white px-4 py-2 rounded hover:bg-gray-700"
            >
              ✍️ Manuāli izveidot čeku
            </button>
          </div>
          {message && <p className="mt-4 font-medium text-green-600">{message}</p>}
          {importMessage && <p className="mt-2 text-blue-700">{importMessage}</p>}
        </div>

        {/* Kategorijas pievienošana */}
        <div className="bg-white rounded-xl shadow p-6 border border-green-200 mb-8">
          <h2 className="text-xl font-semibold text-green-800 mb-4">➕ Pievienot jaunu kategoriju</h2>
          <div className="flex flex-col sm:flex-row gap-4 items-center">
            <input
              type="text"
              value={categoryName}
              onChange={(e) => setCategoryName(e.target.value)}
              placeholder="Kategorijas nosaukums"
              className="border border-gray-300 p-2 rounded w-full sm:w-auto"
            />
            <button
              onClick={handleAddCategory}
              className="bg-green-600 text-white px-6 py-2 rounded hover:bg-green-700"
            >
              Pievienot
            </button>
          </div>
          {categoryMessage && <p className="mt-4 text-green-700">{categoryMessage}</p>}
        </div>

        {/* Esošās kategorijas */}
<div className="bg-white rounded-xl shadow p-6 border border-gray-200">
  <h2 className="text-lg font-semibold text-gray-700 mb-4">📚 Esošās kategorijas</h2>
  {categories.length === 0 ? (
    <p className="text-gray-500">Nav nevienas kategorijas.</p>
  ) : (
    <ul className="space-y-3 text-sm">
      {categories.map((cat) => (
        <li key={cat.id} className="flex items-center gap-2">
          <input
            type="text"
            value={cat.name}
            onChange={(e) => {
              const updated = categories.map((c) =>
                c.id === cat.id ? { ...c, name: e.target.value } : c
              );
              setCategories(updated);
            }}
            className="border p-1 rounded w-full max-w-xs"
          />
          <button
            onClick={() => handleUpdateCategory(cat.id, cat.name)}
            className="bg-blue-500 text-white px-2 py-1 rounded text-xs hover:bg-blue-600"
          >
            💾 Saglabāt
          </button>
          <button
            onClick={() => handleDeleteCategory(cat.id)}
            className="bg-red-500 text-white px-2 py-1 rounded text-xs hover:bg-red-600"
          >
            🗑️ Dzēst
          </button>
        </li>
      ))}
    </ul>
  )}
</div>
      </div>
    </div>
  );
}

export default AddReceipt;
