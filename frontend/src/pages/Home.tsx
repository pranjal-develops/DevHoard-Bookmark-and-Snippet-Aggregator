import { useEffect, useState } from 'react';
import Sidebar from '../components/Sidebar';
import UrlForm from '../components/UrlForm';
import Card from '../components/Card';
import axios from 'axios';
import Search from '../components/Search';

export default function App() {
    const [url, setUrl] = useState('');

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            await axios.post("http://localhost:8080/api/bookmarks", { url });
            setUrl("");
            window.location.reload();
        } catch (error) {
            console.log("Error saving bookmark", error);
        }
    };

    const [bookmarks, setBookmarks] = useState<any[]>([])

    // useEffect(() => {
    //     const fetchData = async () => {
    //         try {
    //             const response = await axios.get("http://localhost:8080/api/bookmarks");
    //             setBookmarks(response.data);
    //         } catch (error) {
    //             console.log(error);
    //         }
    //     }
    //     fetchData();
    // }, [])

    return (
        <div className="flex bg-slate-50 min-h-screen text-slate-800">


            <Sidebar />
            {/* Main Content Area */}
            <main className="flex-1 flex flex-col items-center pt-10 px-6">
                <Search setBookmarks={setBookmarks} />
                <h1 className="text-4xl font-extrabold text-slate-900 mb-8 tracking-tight">Save a Bookmark</h1>


                <UrlForm url={url} setUrl={setUrl} handleSubmit={handleSubmit} />

                {/* The Grid where the cards will go */}
                <div className="w-full max-w-5xl grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {bookmarks.map((b: any) => (
                        <Card key={b.id} bookmark={b} />
                    ))}

                </div>
            </main>
        </div>
    );
}
