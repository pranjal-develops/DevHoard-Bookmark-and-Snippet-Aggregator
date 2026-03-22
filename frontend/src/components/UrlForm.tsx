import React from 'react'

interface UrlFormProps {
    url: string
    setUrl: (url: string) => void;
    handleSubmit: (e: React.FormEvent) => void;
}

{/* The URL Form */ }
const UrlForm = ({ url, setUrl, handleSubmit }: UrlFormProps) => {
    return (
        <form onSubmit={handleSubmit} className="w-full max-w-2xl flex gap-3 mb-12">
            <input
                type="url"
                required
                placeholder="Paste a tutorial URL (e.g. https://spring.io)"
                className="flex-1 px-5 py-4 rounded-xl border border-slate-300 focus:outline-none focus:ring-4 focus:ring-blue-500/20 focus:border-blue-500 shadow-sm transition-all"
                value={url}
                onChange={(e) => setUrl(e.target.value)}
            />
            <button
                type="submit"
                className="px-8 py-4 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded-xl shadow-lg hover:shadow-xl transition-all active:scale-95"
            >
                Scrape!
            </button>
        </form>
    )
}

export default UrlForm