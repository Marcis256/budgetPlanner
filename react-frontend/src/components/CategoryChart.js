import React, { useEffect, useState } from 'react';
import { Doughnut } from 'react-chartjs-2';
import { Chart, ArcElement, Tooltip, Legend } from 'chart.js';
import axios from 'axios';

Chart.register(ArcElement, Tooltip, Legend);

function CategoryChart({ data: filteredData }) {
  const [chartData, setChartData] = useState(null);

  useEffect(() => {
    const loadData = async () => {
      let dataToUse = filteredData;

      if (!filteredData) {
        try {
          const res = await axios.get('http://localhost:8080/api/stats/categories');
          dataToUse = res.data;
        } catch (err) {
          console.error('Neizdevās ielādēt datus:', err);
          return;
        }
      }

      const labels = dataToUse.map(item =>
        `${item.category} – ${Number(item.total).toFixed(2)} €`
      );
      const totals = dataToUse.map(item => item.total);

      setChartData({
        labels,
        datasets: [
          {
            label: 'Iztērēts (€)',
            data: totals,
            backgroundColor: [
              '#60A5FA', '#34D399', '#FBBF24', '#F87171',
              '#A78BFA', '#F472B6', '#10B981', '#FCD34D'
            ],
            borderColor: '#fff',
            borderWidth: 2,
          },
        ],
      });
    };

    loadData();
  }, [filteredData]);

  return (
    <div className="max-w-md mx-auto bg-white rounded shadow p-6 mt-8">
      <h2 className="text-xl font-semibold text-center mb-4">📊 Kategoriju diagramma</h2>
      {chartData ? (
        <div style={{ width: '250px', height: '250px' }} className="mx-auto">
          <Doughnut data={chartData} options={{ maintainAspectRatio: false }} />
        </div>
      ) : (
        <p className="text-center text-gray-500">Notiek ielāde...</p>
      )}
    </div>
  );
}

export default CategoryChart;
