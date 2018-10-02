#pragma once



namespace NestLoadShaping
{

class PreparationOption
{
    PreparationOption( const std::string & name );

    std::string _name;
    unsigned    _value;

    typedef std::map<std::string, PreparationOption*>   Cache;

    static Cache _lookup;

public:

    static const PreparationOption & Lookup( const std::string & name );

    std::string getName()  const { return _name;    }
    unsigned    getValue() const { return _value;   }

    static const PreparationOption
        Standard,
        None,
        Ramping;
};

inline bool operator==( const PreparationOption & lhs, const PreparationOption & rhs )
{
    return lhs.getValue() == rhs.getValue();
}

inline bool operator<( const PreparationOption & lhs, const PreparationOption & rhs )
{
    return lhs.getValue() < rhs.getValue();
}


/// 


class PeakOption
{
    PeakOption( const std::string & name );

    std::string _name;
    unsigned    _value;

    typedef std::map<std::string, PeakOption*>   Cache;

    static Cache _lookup;

public:

    static const PeakOption & Lookup( const std::string & name );

    std::string getName()  const { return _name;    }
    unsigned    getValue() const { return _value;   }

    static const PeakOption
        Standard,
        Uniform,
        Symmetric;
};

inline bool operator==( const PeakOption & lhs, const PeakOption & rhs )
{
    return lhs.getValue() == rhs.getValue();
}

inline bool operator<( const PeakOption & lhs, const PeakOption & rhs )
{
    return lhs.getValue() < rhs.getValue();
}


/// 


class PostOption
{
    PostOption( const std::string & name );

    std::string _name;
    unsigned    _value;

    typedef std::map<std::string, PostOption*>   Cache;

    static Cache _lookup;

public:

    static const PostOption & Lookup( const std::string & name );

    std::string getName()  const { return _name;    }
    unsigned    getValue() const { return _value;   }

    static const PostOption
        Standard,
        Ramping;
};

inline bool operator==( const PostOption & lhs, const PostOption & rhs )
{
    return lhs.getValue() == rhs.getValue();
}

inline bool operator<( const PostOption & lhs, const PostOption & rhs )
{
    return lhs.getValue() < rhs.getValue();
}

}

