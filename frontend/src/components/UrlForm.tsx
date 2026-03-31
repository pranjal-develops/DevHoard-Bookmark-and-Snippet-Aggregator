import React from 'react'
import TagInput from './TagInput';

interface UrlFormProps {
    url: string;
    categories: string[];
    setUrl: (url: string) => void;
    setCategories: (categories: string[]) => void;
    handleSubmit: (e: React.FormEvent) => void;
    isSubmitting: boolean;
}


{/* The URL Form */ }
const UrlForm = ({ url, setUrl, handleSubmit, isSubmitting, categories, setCategories }: UrlFormProps) => {
    return (
        <form onSubmit={handleSubmit} className="w-full max-w-2xl flex flex-col sm:flex-row gap-3 mb-12">
            <input
                type="url"
                required
                placeholder="Paste a tutorial URL (e.g. https://spring.io)"
                className="flex-1 px-5 py-4 rounded-xl bg-white dark:bg-[hsl(0,0%,10%)] border border-zinc-300 dark:border-zinc-700 focus:outline-none focus:ring-4 focus:ring-lime-500/20 dark:focus:ring-lime-400/20 focus:border-lime-500 dark:focus:border-lime-400 shadow-sm transition-all font-mono text-sm"
                value={url}
                onChange={(e) => setUrl(e.target.value)}
            />
            <TagInput tags={categories} setTags={setCategories} placeHolder="Category (e.g. AI, Docs, Dev)" inForm={true} />

            {
                isSubmitting ? (
                    <button
                        type="submit"
                        className="px-8 py-4 bg-lime-600 dark:bg-lime-500 dark:hover:bg-lime-400 dark:text-black hover:bg-lime-500 text-white font-bold tracking-wider rounded-xl shadow-[0_0_15px_rgba(5,150,105,0.4)] hover:shadow-[0_0_25px_rgba(5,150,105,0.6)] transition-all active:scale-95 uppercase text-sm"
                    >
                        Scrapeing...
                    </button>
                )
                    :
                    (
                        <button
                            type="submit"
                            className="px-8 py-4 bg-lime-600 dark:bg-lime-500 dark:hover:bg-lime-400 dark:text-black hover:bg-lime-500 text-white font-bold tracking-wider rounded-xl shadow-[0_0_15px_rgba(5,150,105,0.4)] hover:shadow-[0_0_25px_rgba(5,150,105,0.6)] transition-all active:scale-95 uppercase text-sm"
                        >
                            Scrape!
                        </button>
                    )
            }
        </form>
    )
}

export default UrlForm