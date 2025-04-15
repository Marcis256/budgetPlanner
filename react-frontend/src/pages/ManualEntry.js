import React, { useState, useEffect } from 'react';
import axios from 'axios';

function ManualReceipt() {
  const [receiptNumber, setReceiptNumber] = useState('');
  const [date, setDate] = useState('');
  const [products, setProducts] = useState([
    { type: 'existing', productId: '', name: '', unitPrice: 0, quantity: 1 }
  ]);
  const [allProducts, setAllProducts] = useState([]);
  const [message, setMessage] = useState('');

  useEffect(() => {
    axios.get('http://localhost:8080/api/products')
      .then(res => setAllProducts(res.data))
      .catch(err => console.error('Neizdevās ielādēt produktus:', err));
  }, []);

    const validateForm = () => {
      if (!receiptNumber.trim()) {
        setMessage('❗ Lūdzu ievadi čeka numuru!');
        return false;
      }

      if (!date) {
        setMessage('❗ Lūdzu izvēlies datumu!');
        return false;
      }

      for (let i = 0; i < products.length; i++) {
        const p = products[i];
        if (p.type === 'existing' && !p.productId) {
          setMessage(`❗ Lūdzu izvēlies esošu produktu rindā ${i + 1}.`);
          return false;
        }
        if (p.type === 'new' && !p.name.trim()) {
          setMessage(`❗ Lūdzu ievadi produkta nosaukumu rindā ${i + 1}.`);
          return false;
        }
        if (p.unitPrice <= 0 || isNaN(p.unitPrice)) {
          setMessage(`❗ Produkta cena nevar būt 0 vai tukša (rinda ${i + 1}).`);
          return false;
        }
        if (p.quantity <= 0 || isNaN(p.quantity)) {
          setMessage(`❗ Produkta daudzums nevar būt 0 vai tukšs (rinda ${i + 1}).`);
          return false;
        }
      }

      return true;
    };

  const addProduct = () => {
    setProducts([...products, { type: 'existing', productId: '', name: '', unitPrice: 0, quantity: 1 }]);
  };

  const toggleProductType = (index) => {
    const updated = [...products];
    updated[index].type = updated[index].type === 'existing' ? 'new' : 'existing';
    updated[index].productId = '';
    updated[index].name = '';
    updated[index].unitPrice = 0;
    setProducts(updated);
  };

  const handleProductChange = (index, field, value) => {
    const updated = [...products];
    if (field === 'productId') {
      const selected = allProducts.find(p => p.id === parseInt(value));
      updated[index].productId = selected?.id;
      updated[index].unitPrice = selected?.unitPrice;
      updated[index].name = selected?.name;
    } else {
      updated[index][field] = field === 'name' ? value : parseFloat(value);
    }
    setProducts(updated);
  };

  const calculateTotal = () => {
    return products.reduce((sum, p) => sum + p.unitPrice * p.quantity, 0).toFixed(2);
  };

    const handleSubmit = async () => {
      if (!validateForm()) return; // ✅ Izsauc validāciju

      const data = {
        receiptNumber,
        date: date + 'T00:00:00',
        total: parseFloat(calculateTotal()),
        products: products.map(p => ({
          productId: p.type === 'existing' ? p.productId : undefined,
          name: p.type === 'new' ? p.name : undefined,
          unitPrice: p.unitPrice,
          quantity: p.quantity,
          totalPrice: parseFloat((p.unitPrice * p.quantity).toFixed(2))
        }))
      };

      try {
        await axios.post('http://localhost:8080/api/receipts/manual', data);
        setMessage('✅ Čeks saglabāts veiksmīgi!');
      } catch (error) {
        setMessage('❌ Kļūda saglabājot čeku.');
        console.error(error);
      }
    };

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">Manuāla čeka izveide</h1>

      <label>Čeka Nr:</label>
      <input type="text" value={receiptNumber} onChange={(e) => setReceiptNumber(e.target.value)} className="block border p-2 mb-2" />

      <label>Datums:</label>
      <input type="date" value={date} onChange={(e) => setDate(e.target.value)} className="block border p-2 mb-4" />

      <h2 className="text-lg font-semibold mb-2">Produkti</h2>
      {products.map((p, idx) => (
        <div key={idx} className="mb-4 border rounded p-4 space-y-2 bg-gray-50">
          <button onClick={() => toggleProductType(idx)} className="text-blue-600 text-sm underline">
            {p.type === 'existing' ? '👉 Pievienot jaunu produktu' : '🔄 Izvēlēties esošu produktu'}
          </button>

          {p.type === 'existing' ? (
            <select
              value={p.productId}
              onChange={(e) => handleProductChange(idx, 'productId', e.target.value)}
              className="border p-2 w-full"
            >
              <option value="">-- Izvēlies produktu --</option>
              {allProducts.map(prod => (
                <option key={prod.id} value={prod.id}>
                  {prod.name} - {prod.unitPrice} €
                </option>
              ))}
            </select>
          ) : (
            <>
              <input
                type="text"
                placeholder="Nosaukums"
                value={p.name}
                onChange={(e) => handleProductChange(idx, 'name', e.target.value)}
                className="border p-2 w-full"
              />
              <input
                type="number"
                placeholder="Cena"
                value={p.unitPrice}
                onChange={(e) => handleProductChange(idx, 'unitPrice', e.target.value)}
                className="border p-2 w-full"
              />
            </>
          )}

          <input
            type="number"
            placeholder="Daudzums"
            value={p.quantity}
            onChange={(e) => handleProductChange(idx, 'quantity', e.target.value)}
            className="border p-2 w-full"
          />

          <div className="text-green-700 font-medium">
            Kopā: {(p.unitPrice * p.quantity).toFixed(2)} €
          </div>
        </div>
      ))}

      <button onClick={addProduct} className="bg-gray-300 px-3 py-1 rounded mb-4">➕ Pievienot produktu</button>

      <div className="font-bold text-green-700 mb-4">💰 Kopā: {calculateTotal()} €</div>

      <button
        onClick={handleSubmit}
        className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
      >
        Saglabāt čeku
      </button>

      {message && <p className="mt-4">{message}</p>}
    </div>
  );
}

export default ManualReceipt;
