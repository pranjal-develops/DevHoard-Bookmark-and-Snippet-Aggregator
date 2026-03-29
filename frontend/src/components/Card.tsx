import axios from 'axios';
import React from 'react'

interface Bookmark {
  id: string;
  title: string;
  description: string;
  imgUrl: string;
  originalUrl: string;
  category: string;
  isFavorite: boolean;
}

interface CardProps {
  bookmark: Bookmark;
  setBookmarks: (value: React.SetStateAction<Bookmark[]>) => void;
}



const Card = ({ bookmark, setBookmarks }: CardProps) => {

  const handleDelete = async (id: string) => {
    try {
      await axios.delete(`http://localhost:8080/api/bookmarks/${id}`);
      setBookmarks(prevBookmarks => prevBookmarks.filter(b => b.id !== id));
    } catch (e) {
      console.log("Error deleting bookmark", e);
    }
  }

  const handleToggleFavorite = async () => {
    try {
      const response = await axios.patch(`http://localhost:8080/api/bookmarks/${bookmark.id}/favorite`);
      setBookmarks(prev => prev.map(b => b.id === bookmark.id ? response.data : b));
    } catch (e) {
      console.log("Error toggling favorite", e);
    }
  };


  const [isEditing, setIsEditing] = React.useState(false);
  const [newCategory, setNewCategory] = React.useState(bookmark.category || "");

  const handleUpdateCategory = async () => {
    try {
      const response = await axios.patch(`http://localhost:8080/api/bookmarks/${bookmark.id}/category`, {
        category: newCategory
      });
      // Update the list so the UI reflects the change
      setBookmarks(prev => prev.map(b => b.id === bookmark.id ? response.data : b));
      setIsEditing(false); // Stop editing
    } catch (e) {
      console.log("Error updating category", e);
    }
  }


  return (
    <div className="group relative bg-white dark:bg-[#0a0a0a] rounded-xl shadow-md border border-zinc-200 dark:border-zinc-800/50 overflow-hidden hover:shadow-[0_0_30px_rgba(163,230,53,0.15)] hover:border-lime-500/50 transition-all duration-500 cursor-pointer">

      {/* --- Image Layer with Scanning Glow --- */}
      <div className="h-44 relative bg-zinc-100 dark:bg-zinc-950 border-b border-zinc-100 dark:border-zinc-900 overflow-hidden">
        <img
          // src={bookmark.imgUrl || 'https://images.unsplash.com/photo-1555066931-4365d14bab8c?auto=format&fit=crop&q=80&w=400'}
          src={bookmark.imgUrl}
          alt={bookmark.title}
          className="w-full h-full object-cover grayscale-20 group-hover:grayscale-0 group-hover:scale-110 transition-all duration-700 opacity-80 group-hover:opacity-100"
        />
        {/* Neon HUD Overlay */}
        <div className="absolute inset-0 bg-linear-to-t from-black/80 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-500 flex items-end p-4">
          <span className="text-[10px] font-mono text-lime-400 font-bold tracking-widest uppercase flex items-center gap-2">
            <span className="w-1.5 h-1.5 bg-lime-400 rounded-full animate-pulse"></span>
            NODE_ACCESS: GRANTED
          </span>
        </div>
      </div>

      {/* --- Data Layer --- */}
      <div className="p-5">
        <div className="flex items-start justify-between mb-2">
          <div className="flex items-center gap-2 mb-2">
            <div className="flex items-center gap-2 mb-2">
              {isEditing ? (
                <input
                  autoFocus
                  className="text-[9px] font-mono px-2 py-0.5 rounded-sm bg-zinc-900 text-lime-400 border border-lime-500/50 outline-none w-24"
                  value={newCategory}
                  onChange={(e) => setNewCategory(e.target.value)}
                  onBlur={handleUpdateCategory}
                  onKeyDown={(e) => e.key === 'Enter' && handleUpdateCategory()}
                />
              ) : (
                <span
                  onClick={(e) => { e.stopPropagation(); setIsEditing(true); }}
                  className="text-[9px] font-mono px-2 py-0.5 rounded-sm bg-lime-500/10 text-lime-500 border border-lime-500/20 uppercase tracking-tighter cursor-edit hover:bg-lime-500/20 transition-all"
                >
                  {bookmark.category || "UNCATEGORIZED"}
                </span>
              )}
            </div>

          </div>

          <h3 className="font-bold text-sm leading-snug line-clamp-2 text-zinc-900 dark:text-zinc-100 group-hover:text-lime-500 transition-colors">
            {bookmark.title || "UNTITLED_NODE"}
          </h3>
        </div>

        <p className="text-zinc-500 dark:text-zinc-500 text-[11px] leading-relaxed line-clamp-2 mb-6 h-8">
          {bookmark.description || "System encountered zero metadata description for this memory fragment."}
        </p>

        {/* Technical Metadata Footer */}
        <div className="flex items-center justify-between pt-3 border-t border-zinc-100 dark:border-zinc-900/50">
          <div className="flex flex-col">
            <span className="text-[8px] font-mono uppercase text-zinc-400 dark:text-zinc-600 tracking-tighter">SOURCE_URL</span>
            <div className="text-[10px] text-zinc-400 dark:text-lime-500/80 font-mono truncate max-w-[140px] lowercase italic">
              {bookmark.originalUrl?.replace(/^https?:\/\//, '')}
            </div>
          </div>

          {/* Cyberpunk Trash Icon (Only visible on hover) */}
          <button
            onClick={(e) => { e.stopPropagation(); handleDelete(bookmark.id); }}
            className="p-2 rounded-md text-zinc-300 dark:text-zinc-800 hover:bg-red-500/10 hover:text-red-500 dark:hover:text-red-400 opacity-0 group-hover:opacity-100 translate-y-2 group-hover:translate-y-0 transition-all duration-300 active:scale-90"
            title="Purge Node"
          >
            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"><path d="M3 6h18" /><path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6" /><path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2" /><line x1="10" y1="11" x2="10" y2="17" /><line x1="14" y1="11" x2="14" y2="17" /></svg>
          </button>
        </div>
      </div>

      {/* Decorative Corner */}
      <div className="absolute top-0 right-0 w-8 h-8 pointer-events-none">
        <div className="absolute top-0 right-0 border-t-2 border-r-2 border-transparent group-hover:border-lime-500/30 transition-all duration-500 w-2 h-2"></div>
      </div>

      {/* Favorite Toggle Icon */}
      {/* Favorite Toggle Icon - Professional Cyberpunk Version */}
      <button
        onClick={(e) => { e.stopPropagation(); handleToggleFavorite(); }}
        className={`absolute top-4 right-4 p-2.5 rounded-xl transition-all duration-300 z-20 group/heart
  ${bookmark.isFavorite
            ? ' text-red-500'
            : 'text-black/40 dark:text-white/40 hover:text-white'}`}
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="18" height="18"
          viewBox="0 0 24 24"
          fill={bookmark.isFavorite ? "currentColor" : "none"}
          stroke="currentColor"
          strokeWidth="2.5"
          strokeLinecap="round"
          strokeLinejoin="round"
          className={`${bookmark.isFavorite ? 'animate-pulse' : 'scale-90 group-hover/heart:scale-110'} transition-transform`}
        >
          <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l8.84-8.84 1.06-1.06a5.5 5.5 0 0 0 0-7.78z" />
        </svg>
      </button>



    </div>


  )
}

export default Card