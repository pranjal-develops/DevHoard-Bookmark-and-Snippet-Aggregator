import React from 'react'

{/* Sidebar Area */ }
const Sidebar = () => {
    return (
        < aside className="hidden md:flex flex-col w-64 bg-slate-900 text-white min-h-screen" >
            <div className="p-6 font-bold text-2xl tracking-wider">DevHoard</div>
            <nav className="flex-1 px-4 space-y-2">
                <a href="#" className="block px-4 py-2 bg-slate-800 rounded-md font-medium text-slate-100">All Bookmarks</a>
                <a href="#" className="block px-4 py-2 hover:bg-slate-800 rounded-md text-slate-400">Favorites</a>
                <a href="#" className="block px-4 py-2 hover:bg-slate-800 rounded-md text-slate-400">Archived</a>
            </nav>
        </aside >
    )
}

export default Sidebar