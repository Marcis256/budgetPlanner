import React, { useEffect, useState } from 'react';
import axios from 'axios';

function Products() {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [selected, setSelected] = useState({}); // productId -> categoryId
  const [saving, setSaving] = useState({}); // productId -> true/false
const [filterCategoryId, setFilterCategoryId] = useState('');

  useEffect(() => {
    fetchProducts();
    fetchCategories();
  }, []);

  const fetchProducts = async () => {
    try {
      const res = await axios.get('http://localhost:8080/api/products');
      setProducts(res.data);
    } catch (err) {
      console.error('Neizdevās ielādēt produktus', err);
    }
  };

  const fetchCategories = async () => {
    try {
      const res = await axios.get('http://localhost:8080/api/categories');
      setCategories(res.data);
    } catch (err) {
      console.error('Neizdevās ielādēt kategorijas', err);
    }
  };

const handleCategoryFilterChange = async (categoryId) => {
  setFilterCategoryId(categoryId);

  if (!categoryId) {
    fetchProducts(); // rāda visus
  } else {
    try {
      const res = await axios.get(`http://localhost:8080/api/products/by-category/${categoryId}`);
      setProducts(res.data);
    } catch (err) {
      console.error('Neizdevās ielādēt filtrētos produktus', err);
    }
  }
};

  const handleCategoryChange = (productId, categoryId) => {
    setSelected((prev) => ({ ...prev, [productId]: categoryId }));
  };

  const saveCategory = async (productId) => {
    if (!selected[productId]) return;
    setSaving((prev) => ({ ...prev, [productId]: true }));

    try {
      await axios.put(`http://localhost:8080/api/products/${productId}/category`, {
        categoryId: selected[productId],
      });
      fetchProducts(); // Refresh data
    } catch (err) {
      console.error('Neizdevās saglabāt kategoriju:', err);
    } finally {
      setSaving((prev) => ({ ...prev, [productId]: false }));
    }
  };

  const saveAllCategories = async () => {
    const changes = Object.entries(selected)
      .filter(([_, categoryId]) => categoryId) // tikai izvēlētie
      .map(([productId, categoryId]) => ({
        productId: parseInt(productId),
        categoryId: parseInt(categoryId),
      }));

    if (changes.length === 0) return;

    try {
      await axios.put('http://localhost:8080/api/products/update-categories', changes);
      fetchProducts(); // atjauno sarakstu
      setSelected({});
    } catch (err) {
      console.error('Neizdevās saglabāt visas kategorijas:', err);
    }
  };

  return (
     <div className="p-6 max-w-6xl mx-auto">
       <h1 className="text-3xl font-bold mb-6 text-center">🛒 Produktu saraksts</h1>

       <div className="mb-4 flex gap-4 items-center">
         <label className="font-medium">Filtrēt pēc kategorijas:</label>
         <select
           value={filterCategoryId}
           onChange={(e) => handleCategoryFilterChange(e.target.value)}
           className="border rounded p-2"
         >
           <option value="">Visas kategorijas</option>
           <option value="none">-- Bez kategorijas --</option>
           {categories.map((cat) => (
             <option key={cat.id} value={cat.id}>
               {cat.name}
             </option>
           ))}
         </select>
       </div>

       {products.length === 0 ? (
         <p className="text-gray-500 text-center">Nav pievienotu produktu.</p>
       ) : (
         <>
           <table className="min-w-full bg-white shadow rounded-lg overflow-hidden">
             <thead>
               <tr className="bg-blue-100 text-gray-700 text-left">
                 <th className="py-3 px-4">Nosaukums</th>
                 <th className="py-3 px-4">Cena (€)</th>
                 <th className="py-3 px-4">Kategorija</th>
                 <th className="py-3 px-4">Darbība</th>
               </tr>
             </thead>
             <tbody>
               {products.map((p) => (
                 <tr key={p.id} className="border-t hover:bg-gray-50">
                   <td className="py-2 px-4">{p.name}</td>
                   <td className="py-2 px-4">{p.unitPrice.toFixed(2)}</td>
                   <td className="py-2 px-4">
                     <select
                       className="border rounded p-1"
                       value={selected[p.id] || p.category?.id || ''}
                       onChange={(e) => handleCategoryChange(p.id, e.target.value)}
                     >
                       <option value="">-- Nav --</option>
                       {categories.map((cat) => (
                         <option key={cat.id} value={cat.id}>
                           {cat.name}
                         </option>
                       ))}
                     </select>
                   </td>
                   <td className="py-2 px-4 text-sm text-gray-500 italic">
                     {selected[p.id] && "Saglabāšanai gatavs"}
                   </td>
                 </tr>
               ))}
             </tbody>
           </table>

           <div className="mt-4 text-right">
             <button
               onClick={saveAllCategories}
               className="bg-green-600 text-white px-5 py-2 rounded hover:bg-green-700"
             >
               💾 Saglabāt visas izmaiņas
             </button>
           </div>
         </>
       )}
     </div>
   );
}

export default Products;
