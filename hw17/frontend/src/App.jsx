import { Navigate, Route, Routes } from 'react-router-dom';
import NavBar from './components/NavBar';
import AuthorForm from './pages/AuthorForm';
import AuthorList from './pages/AuthorList';
import AuthorView from './pages/AuthorView';
import BookForm from './pages/BookForm';
import BookList from './pages/BookList';
import BookView from './pages/BookView';
import GenreForm from './pages/GenreForm';
import GenreList from './pages/GenreList';
import GenreView from './pages/GenreView';

function App() {
  return (
    <div>
      <NavBar />
      <div style={{ padding: '20px' }}>
        <Routes>
          <Route path="/" element={<Navigate to="/books" replace />} />
          
          <Route path="/books" element={<BookList />} />
          <Route path="/books/create" element={<BookForm />} />
          <Route path="/books/:id" element={<BookView />} />
          <Route path="/books/:id/edit" element={<BookForm />} />
          
          <Route path="/authors" element={<AuthorList />} />
          <Route path="/authors/create" element={<AuthorForm />} />
          <Route path="/authors/:id" element={<AuthorView />} />
          <Route path="/authors/:id/edit" element={<AuthorForm />} />
          
          <Route path="/genres" element={<GenreList />} />
          <Route path="/genres/create" element={<GenreForm />} />
          <Route path="/genres/:id" element={<GenreView />} />
          <Route path="/genres/:id/edit" element={<GenreForm />} />
        </Routes>
      </div>
    </div>
  );
}

export default App;