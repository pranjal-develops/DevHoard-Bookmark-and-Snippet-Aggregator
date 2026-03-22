import axios from 'axios';
import React from 'react'

interface CardProps {
  bookmark: any;
}

const handleDelete = async (id: string) => {
  try {
    await axios.delete(`http://localhost:8080/api/bookmarks/${id}`);
    window.location.reload();
  } catch (e) {
    console.log("Error deleting bookmark", e);
  }
}


const Card = ({ bookmark }: CardProps) => {
  return (
    <div className="relative bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden hover:shadow-md transition-shadow group cursor-pointer">
      <div className="h-48 bg-slate-200 overflow-hidden">
        <img src={bookmark.imgUrl} alt="Preview" className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500" />
      </div>
      <div className="p-5">
        <h3 className="font-bold text-lg mb-2 line-clamp-2">{bookmark.title}</h3>
        <p className="text-slate-500 text-sm line-clamp-3 mb-4">{bookmark.description}</p>
        <div className="text-xs text-slate-400 truncate">{bookmark.originalUrl}</div>
      </div>
      <button className="absolute bottom-2 right-2 bg-red-600 hover:bg-red-500 text-white px-2 py-1 rounded-lg cursor-pointer" onClick={() => handleDelete(bookmark.id)}>Delete</button>
    </div>
  )
}

export default Card