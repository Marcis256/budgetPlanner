import React, { useEffect, useState } from 'react';
import axios from 'axios';
import CategoryChart from '../components/CategoryChart';

function CategoryStats() {
  const [categories, setCategories] = useState([]);
  const [selected, setSelected] = useState([]);
  const [filteredStats, setFilteredStats] = useState([]);
  const [total, setTotal] = useState(0);
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [allTime, setAllTime] = useState(true);
  const [categoryProducts, setCategoryProducts] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState('');

  useEffect(() => {
    axios.get('http://localhost:8080/api/categories')
      .then(res => {
        const allNames = res.data.map(cat => cat.name);
        setCategories(res.data);
        setSelected(allNames);
        fetchStatsFor(allNames);
      })
      .catch(err => console.error('Neizdevās ielādēt kategorijas', err));
  }, []);

  const toggleCategory = (category) => {
    setSelected((prev) =>
      prev.includes(category)
        ? prev.filter(c => c !== category)
        : [...prev, category]
    );
  };

const fetchStatsFor = (selectedCategories) => {
  let url = 'http://localhost:8080/api/stats/categories/filter';

  if (!allTime && startDate && endDate) {
    url += `?start=${startDate}T00:00:00&end=${endDate}T23:59:59`;
  }

  axios.post(url, JSON.stringify(selectedCategories), {
      headers: { 'Content-Type': 'application/json' },
  })
    .then(res => {
      setFilteredStats(res.data.categories);
      setTotal(res.data.total);
    })
    .catch(err => {
      console.error('Neizdevās ielādēt statistiku', err);
    });
};

const fetchProductsByCategory = async (category) => {
  let url = 'http://localhost:8080/api/stats/categories/products/filter';

  if (!allTime && startDate && endDate) {
    url += `?start=${startDate}T00:00:00&end=${endDate}T23:59:59`;
  }

  try {
    const res = await axios.get(url);
      const found = res.data.find(item => item.category === category);
    setSelectedCategory(category);
      setCategoryProducts(found ? found.products : []);
  } catch (err) {
    console.error('Neizdevās ielādēt produktus:', err);
    setSelectedCategory('');
    setCategoryProducts([]);
  }
};

  const handleYearChange = (year) => {
    const newStart = `${year}-${startDate.slice(5) || '01-01'}`;
    const newEnd = `${year}-${endDate.slice(5) || '12-31'}`;
    setStartDate(newStart);
    setEndDate(newEnd);
  };

  const handleMonthChange = (month) => {
    const year = startDate.slice(0, 4) || '2025';
    const daysInMonth = new Date(year, month, 0).getDate();
    setStartDate(`${year}-${month}-01`);
    setEndDate(`${year}-${month}-${daysInMonth}`);
  };

  return (
    <div className="max-w-3xl mx-auto p-6">
      <h1 className="text-3xl font-bold mb-6 text-center">📊 Iztērētā summa pēc kategorijas</h1>

      {/* Kategoriju atlase */}
      <div className="mb-6">
        <h2 className="text-lg font-semibold mb-2">Izvēlies kategorijas:</h2>
        <div className="flex flex-wrap gap-2 mb-4">
          {categories.map((cat) => (
            <label key={cat.id} className="flex items-center gap-1">
              <input
                type="checkbox"
                value={cat.name}
                checked={selected.includes(cat.name)}
                onChange={() => toggleCategory(cat.name)}
              />
              <span className="text-sm">{cat.name}</span>
            </label>
          ))}
        </div>

  <div className="flex flex-wrap gap-4 mb-4 items-center">
    <label className="flex items-center gap-2">
      <input
        type="checkbox"
        checked={allTime}
        onChange={() => setAllTime(!allTime)}
      />
      <span className="text-sm">Skatīties visā periodā</span>
    </label>

    <div>
      <label className="text-sm font-medium">No:</label>
      <input
        type="date"
        value={startDate}
        onChange={(e) => setStartDate(e.target.value)}
        className="ml-2 border p-1 rounded"
        disabled={allTime}
      />
    </div>

    <div>
      <label className="text-sm font-medium">Līdz:</label>
      <input
        type="date"
        value={endDate}
        onChange={(e) => setEndDate(e.target.value)}
        className="ml-2 border p-1 rounded"
        disabled={allTime}
      />
    </div>

    <button
      onClick={() => fetchStatsFor(selected)}
      className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
    >
      Parādīt statistiku
    </button>
  </div>

  {!allTime && (
    <div className="flex gap-2 items-center">
      <label className="text-sm font-medium">Gads:</label>
      <select
        value={startDate.slice(0, 4)}
              onChange={(e) => handleYearChange(e.target.value)}
        className="border p-1 rounded"
      >
        {[2023, 2024, 2025].map((y) => (
          <option key={y} value={y}>{y}</option>
        ))}
      </select>

      <label className="text-sm font-medium">Mēnesis:</label>
      <select
        value={startDate.slice(5, 7)}
              onChange={(e) => handleMonthChange(e.target.value)}
        className="border p-1 rounded"
      >
        {[...Array(12).keys()].map((m) => {
          const value = String(m + 1).padStart(2, '0');
          return <option key={value} value={value}>{value}</option>;
        })}
      </select>
    </div>
  )}
      </div>

      {/* Statistika tabula */}
      {filteredStats.length > 0 && (
        <>
          <table className="w-full bg-white rounded shadow">
            <thead className="bg-green-100">
              <tr>
                <th className="py-3 px-4 text-left">Kategorija</th>
                <th className="py-3 px-4 text-left">Iztērēts (€)</th>
              </tr>
            </thead>
            <tbody>
              {filteredStats.map((item, idx) => (
                <tr key={idx} className="border-t">
                  <td
                    className="py-2 px-4 text-blue-700 underline cursor-pointer hover:text-blue-900"
                    onClick={() => fetchProductsByCategory(item.category)}
                  >
                    {item.category}
                  </td>
                  <td className="py-2 px-4">{item.total.toFixed(2)} €</td>
                </tr>
              ))}
            </tbody>
          </table>

          {/* Kopējā summa */}
          <div className="mt-4 text-right text-green-700 font-bold text-lg">
            💰 Kopā: {total.toFixed(2)} €
          </div>

          {/* Produkti izvēlētajā kategorijā */}
          {selectedCategory && (
            <div className="mt-6">
              <h3 className="text-lg font-semibold mb-2 text-gray-800">
                🛒 Produkti kategorijā: {selectedCategory}
              </h3>
              {categoryProducts.length === 0 ? (
                <p className="text-gray-500">Nav atrastu produktu.</p>
              ) : (
            <table className="w-full bg-white rounded shadow text-sm">
              <thead className="bg-gray-100">
                <tr>
                  <th className="py-2 px-4 text-left">Nosaukums</th>
                  <th className="py-2 px-4 text-left">Cena (€)</th>
                  <th className="py-2 px-4 text-left">Daudzums</th>
                      <th className="py-2 px-4 text-left">Atlaide (€)</th>
                  <th className="py-2 px-4 text-left">Kopā (€)</th>
                </tr>
              </thead>
              <tbody>
                {categoryProducts.map((prod, idx) => (
                  <tr key={idx} className="border-t">
                    <td className="py-1 px-4">{prod.name}</td>
                    <td className="py-1 px-4">{prod.unitPrice.toFixed(2)}</td>
                    <td className="py-1 px-4">{prod.quantity}</td>
                        <td className="py-1 px-4">{prod.discountAmount ? `-${prod.discountAmount.toFixed(2)}` : '—'}</td>
                    <td className="py-1 px-4">{prod.total.toFixed(2)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
              )}
            </div>
          )}
        </>
      )}

      <CategoryChart data={filteredStats.length > 0 ? filteredStats : null} />
    </div>
  );
}

export default CategoryStats;
