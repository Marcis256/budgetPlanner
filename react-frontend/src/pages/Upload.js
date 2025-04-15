import React, { useState } from 'react';
import axios from 'axios';

function Upload() {
  const [file, setFile] = useState(null);
  const [message, setMessage] = useState('');

  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
  };

const handleUpload = async () => {
  if (!file) return;

  const formData = new FormData();
  formData.append('file', file);

  try {
    const response = await axios.post('http://localhost:8080/api/receipts/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
    setMessage('✅ Čeks augšupielādēts veiksmīgi!');
    console.log(response.data);
  } catch (error) {
    if (error.response && error.response.status === 409) {
      setMessage('⚠️ Čeks ar šo numuru jau ir augšupielādēts!');
    } else if (error.response && error.response.status === 400) {
      setMessage('❌ Neizdevās nolasīt čeku (attēla kļūda).');
    } else {
      setMessage('❌ Kļūda augšupielādējot čeku.');
    }
    console.error(error);
  }
};

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">Upload Receipt</h1>
      <input type="file" accept="application/pdf" onChange={handleFileChange} className="mb-4" />
      <br />
      <button
        onClick={handleUpload}
        className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
      >
        Upload
      </button>
      {message && <p className="mt-4 text-green-600">{message}</p>}
    </div>
  );
}

export default Upload;
