import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

function NavBar() {
  const { isAuthenticated, username, loading, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  if (!isAuthenticated) {
    return null;
  }

  return (
    <nav>
      <h2>Библиотека</h2>
      <div>
        <Link to="/books">Книги</Link> |{' '}
        <Link to="/authors">Авторы</Link> |{' '}
        <Link to="/genres">Жанры</Link>
        <span style={{ marginLeft: '20px' }}>
          Привет, {username}!
        </span>
        <button onClick={handleLogout} style={{ marginLeft: '10px' }}>
          Выйти
        </button>
      </div>
      <hr />
    </nav>
  );
}

export default NavBar;