import React, { useState } from 'react'
import Tag from './Tag';

interface TagInputProps {
    tags: string[];
    setTags: (tags: string[]) => void;
    placeHolder?: string;
    inForm: boolean;
}

const TagInput: React.FC<TagInputProps> = ({ tags, setTags, placeHolder, inForm }) => {

    const [value, setValue] = useState('');

    const set = new Set(tags);

    return (
        <div className='flex flex-wrap items-center gap-2'>
            {tags.map((tag, index) => (
                <Tag key={index} name={tag} onClick={() => setTags(tags.filter((_, i) => i !== index))} />
            ))}

            <input value={value} onChange={(e) => setValue(e.target.value)} type="text" placeholder={placeHolder} className={inForm ?
                'px-5 py-4 rounded-xl bg-white dark:bg-[hsl(0,0%,10%)] border border-zinc-300 dark:border-zinc-700 focus:outline-none focus:ring-4 focus:ring-lime-500/20 focus:border-lime-500 dark:focus:border-lime-400 transition-all font-mono text-sm'
                :
                'flex-1 px-2 py-1 rounded-lg bg-zinc-100 dark:bg-black border border-zinc-200 dark:border-zinc-800 focus:outline-none focus:ring-2 focus:ring-lime-500/20 focus:border-lime-500 dark:focus:border-lime-400 transition-all font-mono text-xs dark:text-zinc-300'} onKeyDown={(e) => {
                    if (e.key === 'Enter' || e.key === ',') {
                        e.preventDefault();
                        if (value.trim() !== '' && !set.has(value.trim())) {
                            setTags([...tags, value.trim()]);
                            setValue('');
                        }
                    }
                }} />
        </div>
    )
}

export default TagInput