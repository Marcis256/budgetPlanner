import React, { useState } from 'react';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import axios from 'axios';

function FilteredReceipts() {
  const [startDate, setStartDate] = useState(null);
  const [endDate, setEndDate] = useState(null);
  const [receipts, setReceipts] = useState([]);
  const [total, setTotal] = useState(0);
  const [message, setMessage] = useState('');

  const fetchFiltered = async () => {
    if (!startDate || !endDate) {
      setMessage('⚠️ Lūdzu izvēlies gan sākuma, gan beigu datumu!');
      return;
    }

    try {
    const isoStart = startDate.toISOString();
    const isoEnd = endDate.toISOString();

      const [receiptsRes, totalRes] = await Promise.all([
        axios.get(`http://localhost:8080/api/receipts/filter?start=${isoStart}&end=${isoEnd}`),
        axios.get(`http://localhost:8080/api/receipts/total/filter?start=${isoStart}&end=${isoEnd}`)
      ]);

      setReceipts(receiptsRes.data);
      setTotal(totalRes.data);
      setMessage('');
    } catch (error) {
      console.error(error);
      setMessage('❌ Neizdevās ielādēt filtrētos datus.');
    }
  };

  return (
    <div className="max-w-4xl mx-auto p-6">
      <h2 className="text-2xl font-bold mb-6 text-center">📅 Filtrēt čekus pēc datuma</h2>

      <div className="flex flex-col sm:flex-row gap-4 items-center justify-center mb-6">
        <div>
          <label className="font-medium">No:</label>
          <DatePicker
            selected={startDate}
            onChange={setStartDate}
            className="border rounded px-2 py-1 ml-2"
            dateFormat="yyyy-MM-dd"
          />
        </div>

        <div>
          <label className="font-medium">Līdz:</label>
          <DatePicker
            selected={endDate}
            onChange={setEndDate}
            className="border rounded px-2 py-1 ml-2"
            dateFormat="yyyy-MM-dd"
          />
        </div>

        <button
          onClick={fetchFiltered}
          className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
        >
          Filtrēt
        </button>
      </div>

      {message && (
        <p className="text-red-600 mb-4 text-center font-medium">{message}</p>
      )}

      {receipts.length > 0 && (
        <>
          <div className="bg-white p-4 rounded shadow border mb-4">
            <h3 className="text-lg font-semibold text-green-700">✅ Atrastie rezultāti:</h3>
            <p className="mt-2 text-right text-xl font-bold text-green-800">
              💰 Kopā šajā periodā: {total.toFixed(2)} €
            </p>
          </div>

          <div className="bg-white p-4 rounded shadow border">
            <table className="w-full text-left text-sm">
              <thead>
                <tr className="bg-gray-100">
                  <th className="py-2 px-2">Čeka Nr.</th>
                  <th className="py-2 px-2">Datums</th>
                  <th className="py-2 px-2">Kopā (€)</th>
                </tr>
              </thead>
              <tbody>
                {receipts.map((r) => (
                  <tr key={r.id} className="border-b hover:bg-gray-50">
                    <td className="py-1 px-2">{r.receiptNumber}</td>
                    <td className="py-1 px-2">{new Date(r.date).toLocaleString()}</td>
                    <td className="py-1 px-2">{r.total.toFixed(2)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </>
      )}
    </div>
  );
}

export default FilteredReceipts;
