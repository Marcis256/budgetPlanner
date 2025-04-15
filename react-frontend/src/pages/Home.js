import React, { useState, useEffect } from 'react';
import axios from 'axios';

function Home() {
  const [receipts, setReceipts] = useState([]);
  const [filtered, setFiltered] = useState(false);
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [totalSum, setTotalSum] = useState(0);
  const [selectedYear, setSelectedYear] = useState('');
  const [selectedMonth, setSelectedMonth] = useState('');

  const fetchReceipts = async () => {
    try {
      const [resReceipts, resTotal] = await Promise.all([
        axios.get('http://localhost:8080/api/receipts'),
        axios.get('http://localhost:8080/api/receipts/total'),
      ]);
      setReceipts(resReceipts.data);
      setTotalSum(resTotal.data);
      setFiltered(false);
    } catch (error) {
      console.error('Neizdevās ielādēt čekus:', error);
    }
  };

  const filterReceipts = async () => {
    if (!startDate || !endDate) {
      alert('Lūdzu ievadi abus datumus!');
      return;
    }

    try {
      const [resReceipts, resTotal] = await Promise.all([
        axios.get(`http://localhost:8080/api/receipts/filter?start=${startDate}T00:00:00&end=${endDate}T23:59:59`),
        axios.get(`http://localhost:8080/api/receipts/total/filter?start=${startDate}T00:00:00&end=${endDate}T23:59:59`)
      ]);
      setReceipts(resReceipts.data);
      setTotalSum(resTotal.data);
      setFiltered(true);
    } catch (error) {
      console.error('Filtrēšana neizdevās:', error);
    }
  };

    const handleDelete = async (id) => {

      try {
        await axios.delete(`http://localhost:8080/api/receipts/${id}`);
        fetchReceipts(); // pārlādē datus
      } catch (err) {
        console.error('Kļūda dzēšot čeku:', err);
      }
    };

  const handleYearMonthChange = (year, month) => {
    if (year && month) {
      const monthPadded = String(month).padStart(2, '0');
      const start = `${year}-${monthPadded}-01`;
      const end = new Date(year, month, 0).toISOString().split('T')[0]; // pēdējā diena mēnesī

      setStartDate(start);
      setEndDate(end);
    }
  };

  useEffect(() => {
    fetchReceipts();
  }, []);

  useEffect(() => {
    if (selectedYear && selectedMonth) {
      handleYearMonthChange(selectedYear, selectedMonth);
    }
  }, [selectedYear, selectedMonth]);

  const currentYear = new Date().getFullYear();
  const years = Array.from({ length: 5 }, (_, i) => currentYear - i);
  const months = [
    { name: 'Janvāris', value: 1 },
    { name: 'Februāris', value: 2 },
    { name: 'Marts', value: 3 },
    { name: 'Aprīlis', value: 4 },
    { name: 'Maijs', value: 5 },
    { name: 'Jūnijs', value: 6 },
    { name: 'Jūlijs', value: 7 },
    { name: 'Augusts', value: 8 },
    { name: 'Septembris', value: 9 },
    { name: 'Oktobris', value: 10 },
    { name: 'Novembris', value: 11 },
    { name: 'Decembris', value: 12 },
  ];

  return (
    <div className="min-h-screen bg-gradient-to-tr from-blue-50 via-white to-green-50 p-6">
      <div className="max-w-4xl mx-auto">
        {/* Filter section */}
        <div className="bg-white rounded-xl shadow-lg p-6 mb-6 border border-green-200">
          <h2 className="text-xl font-semibold mb-4 text-green-700">📅 Filtrēt pēc datuma</h2>
          <div className="flex flex-wrap items-center gap-4">
            <input
              type="date"
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
              className="border p-2 rounded"
            />
            <span className="text-gray-500">līdz</span>
            <input
              type="date"
              value={endDate}
              onChange={(e) => setEndDate(e.target.value)}
              className="border p-2 rounded"
            />
            <button
              onClick={filterReceipts}
              className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700"
            >
              Filtrēt
            </button>
            {filtered && (
              <button
                onClick={fetchReceipts}
                className="bg-gray-300 text-gray-800 px-4 py-2 rounded hover:bg-gray-400"
              >
                Rādīt visus
              </button>
            )}
          </div>

          {/* Year/Month dropdowns */}
          <div className="flex gap-4 mt-4">
            <select
              value={selectedYear}
              onChange={(e) => setSelectedYear(e.target.value)}
              className="border p-2 rounded"
            >
              <option value="">Gads</option>
              {years.map((year) => (
                <option key={year} value={year}>{year}</option>
              ))}
            </select>
            <select
              value={selectedMonth}
              onChange={(e) => setSelectedMonth(e.target.value)}
              className="border p-2 rounded"
            >
              <option value="">Mēnesis</option>
              {months.map((month) => (
                <option key={month.value} value={month.value}>{month.name}</option>
              ))}
            </select>
          </div>
        </div>

        {/* Receipts list */}
        <div className="space-y-6">
          <h2 className="text-2xl font-semibold text-green-700 mb-2">📋 Ievietotie čeki</h2>

          {receipts.length === 0 ? (
            <p className="text-gray-500">🔍 Nav neviena čeka.</p>
          ) : (
            <>
              {receipts.map((receipt, index) => (
                <React.Fragment key={receipt.id}>
                  <div className="bg-white rounded-xl shadow-md border border-gray-200 p-5">
                    <div className="mb-2">
                      <p className="text-lg font-semibold text-gray-800">
                        Čeks #{receipt.receiptNumber}
                      </p>
                      <p className="text-sm text-gray-500">
                        {new Date(receipt.date).toLocaleString()}
                      </p>
                    </div>
                      <button
                        onClick={() => handleDelete(receipt.id)}
                        className="bg-red-500 text-white px-3 py-1 rounded text-sm hover:bg-red-600"
                      >
                        🗑️ Dzēst
                      </button>
                    <p className="font-bold text-right text-xl text-green-700 mb-2">
                      Kopā: {receipt.total} €
                    </p>

                    {receipt.products && receipt.products.length > 0 && (
                      <div className="overflow-x-auto">
                        <table className="min-w-full text-sm text-left mt-4 border-t pt-2">
                        <thead>
                          <tr className="text-gray-600">
                            <th className="py-2 pr-4">Produkts</th>
                            <th className="py-2 pr-4">Cena</th>
                            <th className="py-2 pr-4">Daudzums</th>
                            <th className="py-2 pr-4">Atlaide</th> {/* ✅ Jauna kolonna */}
                            <th className="py-2">Kopā</th>
                          </tr>
                        </thead>
                        <tbody>
                          {receipt.products.map((item, idx) => (
                            <tr key={idx} className="border-b">
                              <td className="py-2 pr-4">{item.product?.name}</td>
                              <td className="py-2 pr-4">{item.product?.unitPrice} €</td>
                              <td className="py-2 pr-4">{item.quantity}</td>
                              <td className="py-2 pr-4">
                                {item.discountAmount ? `-${item.discountAmount} €` : '—'}
                              </td> {/* ✅ Atlaide */}
                              <td className="py-2">{item.totalPrice} €</td>
                            </tr>
                          ))}
                        </tbody>
                        </table>
                      </div>
                    )}
                  </div>
                  {index < receipts.length - 1 && (
                    <hr className="border-t-2 border-gray-200 my-6" />
                  )}
                </React.Fragment>
              ))}

              {/* Kopējā summa */}
              <div className="mt-8 text-right">
                <p className="text-xl font-bold text-green-800">
                  💰 Kopējā summa: {totalSum.toFixed(2)} €
                </p>
              </div>
            </>
          )}
        </div>
      </div>
    </div>
  );
}

export default Home;
