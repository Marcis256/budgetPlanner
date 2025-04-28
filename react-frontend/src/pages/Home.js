import React, { useState, useEffect, useCallback, useMemo } from 'react';
import axios from 'axios';

function Home() {
  const [receipts, setReceipts] = useState([]);
  const [filtered, setFiltered] = useState(false);
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [totalSum, setTotalSum] = useState(0);
  const [selectedYear, setSelectedYear] = useState('');
  const [selectedMonth, setSelectedMonth] = useState('');

  const fetchData = useCallback(async (start = null, end = null) => {
    const baseUrl = 'http://localhost:8080/api/receipts';
    const timeSuffix = 'T00:00:00';
    const endTimeSuffix = 'T23:59:59';

    try {
      const [resReceipts, resTotal] = await Promise.all([
        axios.get(start && end ? `${baseUrl}/filter?start=${start}${timeSuffix}&end=${end}${endTimeSuffix}` : baseUrl),
        axios.get(start && end ? `${baseUrl}/total/filter?start=${start}${timeSuffix}&end=${end}${endTimeSuffix}` : `${baseUrl}/total`)
      ]);
      setReceipts(resReceipts.data);
      setTotalSum(resTotal.data);
      setFiltered(!!(start && end));
    } catch (error) {
      console.error('Neizdevās ielādēt čekus:', error);
    }
  }, []);

    const handleDelete = async (id) => {
      try {
        await axios.delete(`http://localhost:8080/api/receipts/${id}`);
      fetchData(startDate, endDate); // saglabā filtrēto stāvokli
      } catch (err) {
        console.error('Kļūda dzēšot čeku:', err);
      }
    };

  const handleFilter = () => {
    if (!startDate || !endDate) return alert('Lūdzu ievadi abus datumus!');
    fetchData(startDate, endDate);
  };

  const handleYearMonthChange = useCallback((year, month) => {
    const paddedMonth = String(month).padStart(2, '0');
    const start = `${year}-${paddedMonth}-01`;
    const end = new Date(year, month, 0).toISOString().split('T')[0];
      setStartDate(start);
      setEndDate(end);
  }, []);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  useEffect(() => {
    if (selectedYear && selectedMonth) {
      handleYearMonthChange(selectedYear, selectedMonth);
    }
  }, [selectedYear, selectedMonth, handleYearMonthChange]);

  const currentYear = new Date().getFullYear();
  const years = useMemo(() => Array.from({ length: 5 }, (_, i) => currentYear - i), [currentYear]);

  const months = useMemo(() => [
    'Janvāris', 'Februāris', 'Marts', 'Aprīlis', 'Maijs', 'Jūnijs',
    'Jūlijs', 'Augusts', 'Septembris', 'Oktobris', 'Novembris', 'Decembris'
  ].map((name, i) => ({ name, value: i + 1 })), []);

  return (
    <div className="min-h-screen bg-gradient-to-tr from-blue-50 via-white to-green-50 p-6">
      <div className="max-w-4xl mx-auto">

        {/* ✅ Filter section */}
        <div className="bg-white rounded-xl shadow-lg p-6 mb-6 border border-green-200">
          <h2 className="text-xl font-semibold mb-4 text-green-700">📅 Filtrēt pēc datuma</h2>
          <div className="flex flex-wrap items-center gap-4">
            <input type="date" value={startDate} onChange={e => setStartDate(e.target.value)} className="border p-2 rounded" />
            <span className="text-gray-500">līdz</span>
            <input type="date" value={endDate} onChange={e => setEndDate(e.target.value)} className="border p-2 rounded" />
            <button onClick={handleFilter} className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700">Filtrēt</button>
            {filtered && (
              <button onClick={() => fetchData()} className="bg-gray-300 text-gray-800 px-4 py-2 rounded hover:bg-gray-400">Rādīt visus</button>
            )}
          </div>

          <div className="flex gap-4 mt-4">
            <select value={selectedYear} onChange={e => setSelectedYear(e.target.value)} className="border p-2 rounded">
              <option value="">Gads</option>
              {years.map(y => <option key={y} value={y}>{y}</option>)}
            </select>
            <select value={selectedMonth} onChange={e => setSelectedMonth(e.target.value)} className="border p-2 rounded">
              <option value="">Mēnesis</option>
              {months.map(m => <option key={m.value} value={m.value}>{m.name}</option>)}
            </select>
          </div>
        </div>

        {/* ✅ Receipts */}
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
                      <p className="text-lg font-semibold text-gray-800">Čeks #{receipt.receiptNumber}</p>
                      <p className="text-sm text-gray-500">{new Date(receipt.date).toLocaleString()}</p>
                    </div>
                    <button onClick={() => handleDelete(receipt.id)} className="bg-red-500 text-white px-3 py-1 rounded text-sm hover:bg-red-600">
                        🗑️ Dzēst
                      </button>
                    <p className="font-bold text-right text-xl text-green-700 mb-2">Kopā: {receipt.total} €</p>

                    {receipt.products?.length > 0 && (
                      <div className="overflow-x-auto">
                        <table className="min-w-full text-sm text-left mt-4 border-t pt-2">
                        <thead>
                          <tr className="text-gray-600">
                            <th className="py-2 pr-4">Produkts</th>
                            <th className="py-2 pr-4">Cena</th>
                            <th className="py-2 pr-4">Daudzums</th>
                              <th className="py-2 pr-4">Atlaide</th>
                            <th className="py-2">Kopā</th>
                          </tr>
                        </thead>
                        <tbody>
                          {receipt.products.map((item, idx) => (
                            <tr key={idx} className="border-b">
                              <td className="py-2 pr-4">{item.product?.name}</td>
                              <td className="py-2 pr-4">{item.product?.unitPrice} €</td>
                              <td className="py-2 pr-4">{item.quantity}</td>
                                <td className="py-2 pr-4">{item.discountAmount ? `-${item.discountAmount} €` : '—'}</td>
                              <td className="py-2">{item.totalPrice} €</td>
                            </tr>
                          ))}
                        </tbody>
                        </table>
                      </div>
                    )}
                  </div>
                  {index < receipts.length - 1 && <hr className="border-t-2 border-gray-200 my-6" />}
                </React.Fragment>
              ))}
              <div className="mt-8 text-right">
                <p className="text-xl font-bold text-green-800">💰 Kopējā summa: {totalSum.toFixed(2)} €</p>
              </div>
            </>
          )}
        </div>
      </div>
    </div>
  );
}

export default Home;
