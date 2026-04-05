import React from 'react'
import TagInput from '../TagInput';
import { useBookmarks } from '../../hooks/useBookmarks';

interface CardDataProps {
    title: string;
    description: string;
    categories: string[];
    setCategories: (tags: string[]) => void;
    isEditing: boolean;
    setIsEditing: (isEditing: boolean) => void;
}


const CardData: React.FC<CardDataProps> = ({ title, description, categories, setCategories, isEditing, setIsEditing }) => {
    const { handleTagClick } = useBookmarks();
    return (
        <>
            {/* 🏷️ THE Category HUD */}
            <div className="flex items-center justify-between mb-3 min-h-[24px]">
                <div className="flex flex-wrap gap-1.5">
                    <TagInput tags={categories} setTags={setCategories} placeHolder='Add Categories' isEditing={isEditing} onTagClick={handleTagClick} />
                </div>

                {/* ⚙️ THE EDIT TRIGGER */}
                <button
                    onClick={(e) => { e.stopPropagation(); setIsEditing(!isEditing); }}
                    className="text-zinc-500 hover:text-lime-500 transition-colors"
                >
                    {isEditing ?
                        <p className='text-xs font-mono uppercase text-zinc-400 dark:text-zinc-600 tracking-tighter'>x</p>
                        :
                        <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"><path d="M12 5v14M5 12h14" /></svg>
                    }
                </button>
            </div>

            <h3 className="font-bold text-sm leading-snug line-clamp-2 text-zinc-900 dark:text-zinc-100 group-hover:text-lime-500 transition-colors">
                {title || "UNTITLED_NODE"}
            </h3>

            <p className="text-zinc-500 dark:text-zinc-500 text-[11px] leading-relaxed line-clamp-2 mb-6 h-8">
                {description || "System encountered zero metadata description for this memory fragment."}
            </p>
        </>
    )
}

export default CardData